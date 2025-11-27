# STAGE BUILD
# Use a imagem Eclipse Temurin JDK 21 como imagem base para a etapa de compilação
FROM eclipse-temurin:21-jdk-ubi10-minimal AS build

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia wrapper do Maven e pom.xml
COPY mvnw pom.xml ./
COPY .mvn/ ./.mvn

# Baixa dependências em cache
RUN ./mvnw dependency:resolve dependency:resolve-plugins -DincludeScope=runtime -B

# Copia o código-fonte
COPY src/ ./src

# Compila e empacota (gera o JAR em /target) - Não utilando -DskipTests
RUN ./mvnw clean package

## STAGE RUN
# Use a imagem Eclipse Temurin JRE 21 como imagem base para a etapa de execução
FROM eclipse-temurin:21-jre-ubi10-minimal

# Define UID E GID para usuário e grupo não-root (1001 - primeiro usuário após o root)
# Define nome do usuáro e grupo não-root
ARG APP_UID=1001 \
    APP_GID=1001 \
    APP_USER=appuser \
    APP_GROUP=appgroup

# Define variáveis de ambiente para o nome do arquivo JAR, porta do servidor e perfil do Spring
ENV JAR_NAME=superatecnologia-desafio-java-0.0.1-SNAPSHOT.jar \
    SERVER_PORT=8082 \
    SPRING_PROFILES_ACTIVE=dev \
    DOCKERIZE_VERSION=v0.9.6 \
    TZ=America/Cuiaba

# Comandos para OS Alpine
# RUN addgroup -S spring && adduser -S -G spring spring && \
#     apk add --no-cache tzdata curl && \
#     curl -L https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
#         | tar -xz -C /usr/local/bin && \
#     cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
#     apk del curl

# Comandos para Red Hat Universal Base Image (Utiliza microdnf)
#
# curl já vem instalado no ambiente (não pode ser removido no final porque alguns outros pacotes (rpm) o utilizam).
# shadow-utils: Utilitário para gerenciamento de usuários e grupos (groupadd, useradd...).
#
# Passos da execução:
# - Atualiza lista de pacotes e realiza instalação de tzdata, shadow-utils e tar
# - Adiciona grupo não-root
# - Adiciona usuário não-rot
#   - Associa ao grupo não-root
#   - Define o diretório home como /opt/app (diretório da aplicação)
#   - Define o shell como sem login
# - Baixa Dockerize via curl e extrai
# - Copia informações de TimeZone
# - Remove pacotes desnecessários ao final
# - Realiza limpeza do gerenciador de pacotes microdnf
RUN microdnf update -y && \
    microdnf install -y \
    tzdata shadow-utils tar && \
    groupadd -g ${APP_GID} ${APP_GROUP} && \
    useradd -u ${APP_UID} -r -g ${APP_GROUP} -d /opt/app -s /sbin/nologin -c "App User" ${APP_USER} && \
    curl -L https://github.com/jwilder/dockerize/releases/download/$DOCKERIZE_VERSION/dockerize-linux-amd64-$DOCKERIZE_VERSION.tar.gz \
         | tar -xz -C /usr/local/bin && \
     cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone && \
     microdnf remove -y shadow-utils tar  && \
    microdnf clean all

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia o arquivo JAR gerado na etapa de build para a etapa de execução
# Maven gera artefatos em /target
COPY --from=build --chown=${APP_USER}:${APP_GROUP} /app/target/$JAR_NAME .

# Copia o script de entrypoint e aplica permissão de execução
COPY --chown=${APP_USER}:${APP_GROUP} --chmod=755 docker-entrypoint.sh ./

# Troca para usuário non-root
USER ${APP_USER}:${APP_GROUP}

# Adiciona um healthcheck para verificar se a aplicação está em execução
HEALTHCHECK --interval=15s --timeout=60s --start-period=10s --retries=3 \
    CMD curl -f http://localhost:$SERVER_PORT/actuator/health | grep -i UP || exit 1

# Expõe a porta do servidor para permitir acesso externo
EXPOSE $SERVER_PORT

# Usa o dockerize para aguardar o banco de dados antes de chamar o script de entrypoint (forma shell para permitir substituição de variáveis)
ENTRYPOINT ["/bin/sh", "-c", "dockerize -wait tcp://${DB_HOST:-localhost}:${DB_PORT:-5432} -timeout 60s ./docker-entrypoint.sh"]
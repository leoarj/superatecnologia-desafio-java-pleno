package com.github.leoarj.superatecnologia.desafio.core.springdoc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Desafio Java Pleno - Supera Tecnologia API")
                        .version("v1")
                        .description("API para gerenciamento de Solicitações de acesso a Módulos")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")
                        )
                )
                .tags(Arrays.asList(
                        new Tag().name("Solicitações").description("Gerencia as solicitações de acesso"),
//                        new Tag().name("Usuários").description("Gerencia os usuários"),
                        new Tag().name("Módulos").description("Consulta de módulos disponíveis")
                ))
                // Configuração do JWT para o botão "Authorize" no futuro
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    /*
     * Customizador para adicionar respostas padrão (400, 404, 500) em todos os endpoints
     * economizando anotações nos controllers.
     */
    @Bean
    public OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
                
                // 500
                if (!operation.getResponses().containsKey("500")) {
                    operation.getResponses().addApiResponse("500", new io.swagger.v3.oas.models.responses.ApiResponse().description("Erro interno no servidor"));
                }
                
                // 400
                if (!operation.getResponses().containsKey("400")) {
                     operation.getResponses().addApiResponse("400", new io.swagger.v3.oas.models.responses.ApiResponse().description("Requisição inválida (Erro de validação ou regra de negócio)"));
                }
            }));
        };
    }
}
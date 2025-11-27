# Makefile para agrupar e facilitar a chamada de comandos de gerenciamento dos containers via Docker Compose

# Arquivos Compose
TOOLS_COMPOSE_FILE = docker-compose-tools.yml
DEV_COMPOSE_FILE = docker-compose-dev.yml
PROD_COMPOSE_FILE = docker-compose.yml

# Arquivos de variáveis de ambiente
DEV_ENV_FILE ?= .env.dev
PROD_ENV_FILE ?= .env

# Alvo padrão
.DEFAULT_GOAL := help

## ========================
## Ambiente de Desenvolvimento - Tools (Serviços auxiliares)
## ========================

up-dev-tools:
	docker compose --env-file $(DEV_ENV_FILE) -f $(TOOLS_COMPOSE_FILE) up

down-dev-tools:
	docker compose --env-file $(DEV_ENV_FILE) -f $(TOOLS_COMPOSE_FILE) down

logs-dev-tools:
	docker compose --env-file $(DEV_ENV_FILE) -f $(TOOLS_COMPOSE_FILE) logs -f

ps-dev-tools:
	docker compose --env-file $(DEV_ENV_FILE) -f $(TOOLS_COMPOSE_FILE) ps

reset-dev-tools:
	docker compose --env-file $(DEV_ENV_FILE) -f $(TOOLS_COMPOSE_FILE) down -v --remove-orphans

## ========================
## Ambiente de Desenvolvimento
## ========================

up-dev:
	docker compose --env-file $(DEV_ENV_FILE) -f $(DEV_COMPOSE_FILE) up --build

down-dev:
	docker compose --env-file $(DEV_ENV_FILE) -f $(DEV_COMPOSE_FILE) down

logs-dev:
	docker compose --env-file $(DEV_ENV_FILE) -f $(DEV_COMPOSE_FILE) logs -f

ps-dev:
	docker compose --env-file $(DEV_ENV_FILE) -f $(DEV_COMPOSE_FILE) ps

reset-dev:
	docker compose --env-file $(DEV_ENV_FILE) -f $(DEV_COMPOSE_FILE) down -v --remove-orphans
	docker compose --env-file $(DEV_ENV_FILE) -f $(DEV_COMPOSE_FILE) up --build

## ========================
## Ambiente de Produção
## ========================

up-prod:
	docker compose -f $(PROD_COMPOSE_FILE) up --build --scale supera-api=3

down-prod:
	docker compose -f $(PROD_COMPOSE_FILE) down

logs-prod:
	docker compose -f $(PROD_COMPOSE_FILE) logs -f

ps-prod:
	docker compose -f $(PROD_COMPOSE_FILE) ps

## ========================
## Utilitário
## ========================

help:
	@echo ""
	@echo "Comandos disponíveis:"
	@echo "  Desenvolvimento (Tools - Dependências):"
	@echo "    make up-dev-tools      -> Sobe containers de desenvolvimento (Apenas serviços auxiliares)"
	@echo "    make down-dev-tools    -> Para containers de desenvolvimento (Apenas serviços auxiliares)"
	@echo "    make logs-dev-tools    -> Segue os logs de desenvolvimento (Apenas serviços auxiliares)"
	@echo "    make ps-dev-tools      -> Lista containers de desenvolvimento (Apenas serviços auxiliares)"
	@echo "    make reset-dev-tools   -> Derruba containers, remove volumes e sobe novamente (Apenas serviços auxiliares)"
	@echo ""
	@echo "  Desenvolvimento:"
	@echo "    make up-dev      -> Sobe containers de desenvolvimento"
	@echo "    make down-dev    -> Para containers de desenvolvimento"
	@echo "    make logs-dev    -> Segue os logs de desenvolvimento"
	@echo "    make ps-dev      -> Lista containers de desenvolvimento"
	@echo "    make reset-dev   -> Derruba containers, remove volumes e sobe novamente"
	@echo ""
	@echo "  Produção:"
	@echo "    make up-prod     -> Sobe containers de produção (detached)"
	@echo "    make down-prod   -> Para containers de produção"
	@echo "    make logs-prod   -> Segue os logs de produção"
	@echo "    make ps-prod     -> Lista containers de produção"
	@echo ""
	@echo "Obs: Edite $(DEV_ENV_FILE) ou $(PROD_ENV_FILE) para trocar as variáveis de ambiente."

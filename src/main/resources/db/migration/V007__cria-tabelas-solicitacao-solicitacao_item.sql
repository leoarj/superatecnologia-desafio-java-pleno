-- Registro da Solicitação de acesso a Módulos para determinado Usuário

CREATE TABLE public.solicitacao
(
    id bigserial NOT NULL,
    usuario_id bigserial NOT NULL,
    justificativa character varying(500) NOT NULL,
    urgente boolean NOT NULL DEFAULT false,
    protocolo character(18) NOT NULL,
    solicitado_em timestamp with time zone NOT NULL,
    CONSTRAINT pk_solicitacao PRIMARY KEY (id),
    CONSTRAINT fk_solicitacao_usuario FOREIGN KEY (usuario_id)
        REFERENCES public.usuario (id)
);

COMMENT ON TABLE public.solicitacao
  IS 'Registro da Solicitação de acesso a Módulos para determinado Usuário.';

ALTER TABLE IF EXISTS public.solicitacao
    OWNER to postgres;

-- Item da Solicitação (Referencia de fato qual Módulo solicitado)

CREATE TABLE public.solicitacao_item
(
    id bigserial NOT NULL,
    solicitacao_id bigserial NOT NULL,
    modulo_id bigserial NOT NULL,
    CONSTRAINT pk_solicitacao_item PRIMARY KEY (id),
    CONSTRAINT fk_solicitacao_item_solicitacao FOREIGN KEY (solicitacao_id)
        REFERENCES public.solicitacao (id),
    CONSTRAINT fk_solicitacao_item_modulo FOREIGN KEY (modulo_id)
        REFERENCES public.modulo (id)
);

COMMENT ON TABLE public.solicitacao_item
  IS 'Item da Solicitação (Referencia de fato qual Módulo solicitado).';

ALTER TABLE IF EXISTS public.solicitacao_item
    OWNER to postgres;
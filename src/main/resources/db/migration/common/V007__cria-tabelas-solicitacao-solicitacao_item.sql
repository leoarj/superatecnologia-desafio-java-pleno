-- Registro da Solicitação de acesso a Módulos para determinado Usuário

CREATE TABLE public.solicitacao
(
    id bigserial NOT NULL,
    usuario_id bigint NOT NULL,
    justificativa character varying(500) NOT NULL,
    urgente boolean NOT NULL DEFAULT false,
    protocolo character varying(20) NOT NULL,
    data_solicitacao timestamp with time zone NOT NULL,
    data_expiracao timestamp with time zone,
    status character varying(20) NOT NULL,
    motivo_rejeicao character varying(255),
    solicitacao_anterior_id bigint,

    CONSTRAINT pk_solicitacao PRIMARY KEY (id),
    CONSTRAINT fk_solicitacao_usuario FOREIGN KEY (usuario_id)
        REFERENCES public.usuario (id),
    CONSTRAINT un_solicitacao_protocolo UNIQUE (protocolo),
    CONSTRAINT fk_solicitacao_solicitacao_anterior FOREIGN KEY (solicitacao_anterior_id)
        REFERENCES public.solicitacao (id)
);

COMMENT ON TABLE public.solicitacao
  IS 'Registro da Solicitação de acesso a Módulos para determinado Usuário.';

-- Item da Solicitação (Referencia de fato qual Módulo solicitado)

CREATE TABLE public.solicitacao_modulo
(
    solicitacao_id bigint NOT NULL,
    modulo_id bigint NOT NULL,

    CONSTRAINT pk_solicitacao_modulo PRIMARY KEY (solicitacao_id, modulo_id),
    CONSTRAINT fk_solicitacao_modulo_solicitacao FOREIGN KEY (solicitacao_id)
        REFERENCES public.solicitacao (id),
    CONSTRAINT fk_solicitacao_modulo_modulo FOREIGN KEY (modulo_id)
        REFERENCES public.modulo (id)
);

COMMENT ON TABLE public.solicitacao_modulo
  IS 'Item da Solicitação (Referencia de fato qual Módulo solicitado).';
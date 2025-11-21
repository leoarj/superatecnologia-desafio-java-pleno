-- Módulo

CREATE TABLE public.modulo
(
    id bigserial NOT NULL,
    nome character varying(60) NOT NULL,
    descricao character varying(255),
    ativo boolean,
    disponivel boolean,
    CONSTRAINT pk_modulo PRIMARY KEY (id),
    CONSTRAINT un_modulo_nome UNIQUE (nome)
);

COMMENT ON TABLE public.modulo
  IS 'Tabela para registro dos módulos de acesso disponíveis.';

ALTER TABLE IF EXISTS public.modulo
    OWNER to postgres;

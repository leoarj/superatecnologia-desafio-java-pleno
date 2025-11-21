-- Usuário

CREATE TABLE public.usuario
(
    id bigserial NOT NULL,
    departamento_id bigserial NOT NULL,
    nome character varying(60) NOT NULL,
    email character varying(255) NOT NULL,
    hash_senha character varying(255) NOT NULL,

    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT fk_usuario_departamento FOREIGN KEY (departamento_id)
        REFERENCES public.departamento (id),
    CONSTRAINT un_usuario_nome UNIQUE (nome),
    CONSTRAINT un_usuario_email UNIQUE (email)
);

COMMENT ON TABLE public.usuario
  IS 'Tabela para registro de Usuários, associados a Departamentos e Módulos.';

ALTER TABLE IF EXISTS public.usuario
    OWNER to postgres;
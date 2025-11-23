-- Departamento (Semelhante a ROLE ou Grupo de Acesso nesse caso)

CREATE TABLE public.departamento
(
    id bigserial NOT NULL,
    nome character varying(60) NOT NULL,
    descricao character varying(255),
    ativo boolean,
    CONSTRAINT pk_departamento PRIMARY KEY (id),
    CONSTRAINT un_departamento_nome UNIQUE (nome)
);

COMMENT ON TABLE public.departamento
  IS 'Tabela para registro de departamentos disponíveis associados aos usuários.';
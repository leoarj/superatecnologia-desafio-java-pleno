-- Relação entre Módulos e Departamentos (Compatibilidade)

CREATE TABLE public.modulo_departamento
(
    modulo_id bigserial NOT NULL,
    departamento_id bigserial NOT NULL,
    CONSTRAINT pk_modulo_departamento PRIMARY KEY (modulo_id, departamento_id),
    CONSTRAINT fk_modulo_departamento_modulo FOREIGN KEY (modulo_id)
        REFERENCES public.modulo (id),
    CONSTRAINT fk_modulo_departamento_departamento FOREIGN KEY (departamento_id)
        REFERENCES public.departamento (id)
);

COMMENT ON TABLE public.modulo_departamento
  IS 'Tabela para registro de compatibilidade entre Módulos e Departamentos.';

ALTER TABLE IF EXISTS public.modulo_departamento
    OWNER to postgres;
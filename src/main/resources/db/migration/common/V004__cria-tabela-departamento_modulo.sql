-- Relação entre Módulos e Departamentos (Compatibilidade)

CREATE TABLE public.departamento_modulo
(
    departamento_id bigint NOT NULL,
    modulo_id bigint NOT NULL,

    CONSTRAINT pk_departamento_modulo PRIMARY KEY (departamento_id, modulo_id),
    CONSTRAINT fk_departamento_modulo_departamento FOREIGN KEY (departamento_id)
        REFERENCES public.departamento (id) ON DELETE CASCADE,
    CONSTRAINT fk_departamento_modulo_modulo FOREIGN KEY (modulo_id)
            REFERENCES public.modulo (id) ON DELETE CASCADE
);

COMMENT ON TABLE public.departamento_modulo
  IS 'Tabela para registro de compatibilidade entre Departamentos e Módulos.';
-- Relação de mutualidade entre Módulos

CREATE TABLE IF NOT EXISTS public.modulo_mutualidade
(
    modulo_id bigint NOT NULL,
    modulo_mutual_id bigint NOT NULL,

    CONSTRAINT pk_modulo_mutualidade PRIMARY KEY (modulo_id, modulo_mutual_id),
    CONSTRAINT fk_modulo FOREIGN KEY (modulo_id)
        REFERENCES public.modulo (id) ON DELETE CASCADE,
    CONSTRAINT fk_modulo_mutual FOREIGN KEY (modulo_mutual_id)
        REFERENCES public.modulo (id) ON DELETE CASCADE
);

COMMENT ON TABLE public.modulo_mutualidade
  IS 'Tabela para registro de mutualidade exclusiva entre módulos.';
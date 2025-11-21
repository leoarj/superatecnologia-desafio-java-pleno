-- Relação entre Usuários e Módulos concedidos

CREATE TABLE public.usuario_modulo
(
    usuario_id bigint NOT NULL,
    modulo_id bigint NOT NULL,

    CONSTRAINT pk_usuario_modulo PRIMARY KEY (usuario_id, modulo_id),
    CONSTRAINT fk_usuario_modulo_usuario FOREIGN KEY (usuario_id)
        REFERENCES public.usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_usuario_modulo_modulo FOREIGN KEY (modulo_id)
            REFERENCES public.modulo (id) ON DELETE CASCADE
);

COMMENT ON TABLE public.usuario_modulo
  IS 'Tabela para registro de acesso a Módulos concedidos para usuarios.';

ALTER TABLE IF EXISTS public.usuario_modulo
    OWNER to postgres;

-- Relação entre Usuários e Departamentos (Semelhante a uma associação entre Usuarios e ROLEs/Grupos?)

--create table public.usuario_departamento
--(
--    usuario_id bigserial NOT NULL,
--    departamento_id bigserial NOT NULL,
--
--    CONSTRAINT pk_usuario_departamento PRIMARY KEY (usuario_id, departamento_id),
--    CONSTRAINT fk_usuario_departamento_usuario FOREIGN KEY (usuario_id)
--        REFERENCES public.usuario (id),
--    CONSTRAINT fk_usuario_departamento_departamento FOREIGN KEY (departamento_id)
--        REFERENCES public.departamento (id)
--)
--
--COMMENT ON TABLE public.usuario_departamento
--  IS 'Tabela para registro de associação entre Usuários e Departamentos.';
--
--ALTER TABLE IF EXISTS public.usuario_departamento
--    OWNER to postgres;
-- Vincula Departamentos aos Módulos permitidos

INSERT INTO public.departamento_modulo (departamento_id, modulo_id) VALUES
-- 1. TI (Todos os módulos: 1 a 10)
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),

-- 2. Financeiro (Portal, Relatórios, Gestão Fin., Aprovador Fin., Solicitante Fin.)
(2, 1), (2, 2), (2, 3), (2, 4), (2, 5),

-- 3. RH (Portal, Relatórios, Admin RH, Colaborador RH)
(3, 1), (3, 2), (3, 6), (3, 7),

-- 4. Operações (Portal, Relatórios, Estoque, Compras)
(4, 1), (4, 2), (4, 8), (4, 9),

-- 5. Outros (Portal, Relatórios)
(5, 1), (5, 2);
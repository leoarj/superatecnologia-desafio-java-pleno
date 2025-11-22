-- Popula as regras de exclusividade (incompatibilidade) entre módulos

INSERT INTO public.modulo_mutualidade (modulo_id, modulo_mutual_id) VALUES
-- Regra: Aprovador Financeiro (4) incompatível com Solicitante Financeiro (5) e vice-versa
(4, 5),
(5, 4),

-- Regra: Administrador RH (6) incompatível com Colaborador RH (7) e vice-versa
(6, 7),
(7, 6)

-- Como é uma regra do domínio, então se o registro existir não precisa falhar (sempre mesma operação/idempotência)
ON CONFLICT (modulo_id, modulo_mutual_id) DO NOTHING;
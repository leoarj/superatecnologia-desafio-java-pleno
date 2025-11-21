-- Popula tabela modulo com dados iniciais e ajusta sequence

INSERT INTO public.modulo (id, nome, descricao, ativo, disponivel) VALUES
(1, 'Portal do Colaborador', 'Acesso para todos os departamentos', true, true),
(2, 'Relatórios Gerenciais', 'Acesso para todos os departamentos', true, true),
(3, 'Gestão Financeira', 'Financeiro, TI', true, true),
(4, 'Aprovador Financeiro', 'Financeiro, TI - Incompatível com módulo #5', true, true),
(5, 'Solicitante Financeiro', 'Financeiro, TI - Incompatível com módulo #4', true, true),
(6, 'Administrador RH', 'RH, TI - Incompatível com módulo #7', true, true),
(7, 'Colaborador RH', 'RH, TI - Incompatível com módulo #6', true, true),
(8, 'Gestão de Estoque', 'Operações, TI', true, true),
(9, 'Compras', 'Operações, TI', true, true),
(10, 'Auditoria', 'Apenas TI', true, true)
ON CONFLICT (id) DO NOTHING;

-- Reinicia a sequência do ID para o próximo valor correto (11) (porque explicitrou os IDs na inclusão)
SELECT setval('public.modulo_id_seq', (SELECT MAX(id) FROM public.modulo));
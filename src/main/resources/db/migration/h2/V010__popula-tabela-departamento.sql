-- Popula tabela departamento com IDs explícitos para garantir integridade nas regras

INSERT INTO public.departamento (id, nome, descricao, ativo) VALUES
(1, 'TI', 'Tecnologia da Informação - Acesso total', true),
(2, 'Financeiro', 'Departamento Financeiro', true),
(3, 'RH', 'Recursos Humanos', true),
(4, 'Operações', 'Operações e Logística', true),
(5, 'Outros', 'Departamentos gerais', true);
-- Popula tabela departamento com IDs explícitos para garantir integridade nas regras

INSERT INTO public.departamento (id, nome, descricao, ativo) VALUES
(1, 'TI', 'Tecnologia da Informação - Acesso total', true),
(2, 'Financeiro', 'Departamento Financeiro', true),
(3, 'RH', 'Recursos Humanos', true),
(4, 'Operações', 'Operações e Logística', true),
(5, 'Outros', 'Departamentos gerais', true);

-- Departamento de TI tem acesso a 10 Módulos (Todos os outros por padrão 5)
UPDATE public.departamento SET limite_modulos = 10 WHERE id = 1;
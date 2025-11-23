-- 1. Solicitação ATIVA (Aprovada) - Acesso ao Portal
INSERT INTO public.solicitacao (id, usuario_id, justificativa, urgente, protocolo, data_solicitacao, data_expiracao, status, motivo_rejeicao, solicitacao_anterior_id)
VALUES (1001, 1, 'Acesso padrão ao portal do colaborador.', false, 'SOL-TEST-0001', CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP + INTERVAL '170 days', 'ATIVO', null, null);

-- Itens da Solicitação 1001 (Módulo 1 - Portal)
INSERT INTO public.solicitacao_modulo (solicitacao_id, modulo_id) VALUES (1001, 1);


-- 2. Solicitação NEGADA - Tentativa de acesso indevido (Financeiro + RH incompatíveis)
INSERT INTO public.solicitacao (id, usuario_id, justificativa, urgente, protocolo, data_solicitacao, data_expiracao, status, motivo_rejeicao, solicitacao_anterior_id)
VALUES (1002, 1, 'Tentativa de acumular funções.', true, 'SOL-TEST-0002', CURRENT_TIMESTAMP - INTERVAL '5 days', null, 'NEGADO', 'O módulo Aprovador Financeiro é incompatível com Solicitante Financeiro.', null);

-- Itens da Solicitação 1002 (Módulo 4 e 5)
INSERT INTO public.solicitacao_modulo (solicitacao_id, modulo_id) VALUES (1002, 4);
INSERT INTO public.solicitacao_modulo (solicitacao_id, modulo_id) VALUES (1002, 5);


-- 3. Solicitação EXPIRADA (Antiga) - Para testar histórico
INSERT INTO public.solicitacao (id, usuario_id, justificativa, urgente, protocolo, data_solicitacao, data_expiracao, status, motivo_rejeicao, solicitacao_anterior_id)
VALUES (1003, 1, 'Solicitação antiga de projeto temporário.', false, 'SOL-TEST-OLD1', CURRENT_TIMESTAMP - INTERVAL '200 days', CURRENT_TIMESTAMP - INTERVAL '20 days', 'ATIVO', null, null);

-- Itens da Solicitação 1003 (Módulo 2 - Relatórios)
INSERT INTO public.solicitacao_modulo (solicitacao_id, modulo_id) VALUES (1003, 2);


-- 4. Solicitação de RENOVAÇÃO (Pendente/Ativa) - Renovando a 1003 (exemplo didático)
INSERT INTO public.solicitacao (id, usuario_id, justificativa, urgente, protocolo, data_solicitacao, data_expiracao, status, motivo_rejeicao, solicitacao_anterior_id)
VALUES (1004, 1, 'Renovação da solicitação SOL-TEST-OLD1', true, 'SOL-TEST-0004', CURRENT_TIMESTAMP - INTERVAL '1 day', CURRENT_TIMESTAMP + INTERVAL '179 days', 'ATIVO', null, 1003);

-- Itens da Solicitação 1004 (Módulo 2 - Relatórios)
INSERT INTO public.solicitacao_modulo (solicitacao_id, modulo_id) VALUES (1004, 2);


-- Ajustar a sequência do ID para não dar conflito com novos inserts via API
SELECT setval('solicitacao_id_seq', (SELECT MAX(id) FROM public.solicitacao));
-- Popula usuários iniciais para cada departamento
-- A senha padrão para todos abaixo é: 123456
-- O hash abaixo ($2a$10$...) é o resultado de BCrypt para '123456'

INSERT INTO public.usuario (id, nome, email, departamento_id, hash_senha) VALUES
-- 1. Usuário de TI (Dept 1) - Admin do sistema
(1, 'Admin TI', 'admin.ti@empresa.com', 1, '$2a$10$N.zmdr9k7uOCQb376NoUnutj8iAt6aBECYnzhUwpo9tQdnTEQLC.6'),

-- 2. Usuário Financeiro (Dept 2)
(2, 'Ana Financeiro', 'ana.fin@empresa.com', 2, '$2a$10$N.zmdr9k7uOCQb376NoUnutj8iAt6aBECYnzhUwpo9tQdnTEQLC.6'),

-- 3. Usuário RH (Dept 3)
(3, 'Carlos RH', 'carlos.rh@empresa.com', 3, '$2a$10$N.zmdr9k7uOCQb376NoUnutj8iAt6aBECYnzhUwpo9tQdnTEQLC.6'),

-- 4. Usuário Operações (Dept 4)
(4, 'Beto Operações', 'beto.ops@empresa.com', 4, '$2a$10$N.zmdr9k7uOCQb376NoUnutj8iAt6aBECYnzhUwpo9tQdnTEQLC.6');
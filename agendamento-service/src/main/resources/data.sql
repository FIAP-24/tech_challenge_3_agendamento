-- Hibernate cria automaticamente essa tabela com @ElementCollection
-- Assumindo que os IDs dos usuários são 1, 2 e 3 respectivamente.
INSERT INTO usuario_roles (usuario_id, roles) VALUES (1, 'MEDICO');
INSERT INTO usuario_roles (usuario_id, roles) VALUES (2, 'ENFERMEIRO');
INSERT INTO usuario_roles (usuario_id, roles) VALUES (3, 'PACIENTE');

-- Você também pode querer inserir um paciente de teste na tabela 'paciente' com ID = 1
INSERT INTO paciente (id, nome, cpf, data_nascimento, ativo, data_criacao) VALUES (1, 'Paciente Teste', '11122233344', '1980-01-01', true, CURRENT_TIMESTAMP);

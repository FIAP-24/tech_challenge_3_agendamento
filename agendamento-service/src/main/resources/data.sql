-- A senha para todos os usuários é "password" (sem aspas), encriptada com BCrypt.
-- O valor encriptado é: $2a$10$N.zmdr9k7edaN1unU5z4/e8aKj024.sCg28V222Ym6S2p1/.b2i.G

-- Inserindo usuários na tabela 'usuario'
INSERT INTO usuario (username, password) VALUES ('medico', '$2a$10$N.zmdr9k7edaN1unU5z4/e8aKj024.sCg28V222Ym6S2p1/.b2i.G');
INSERT INTO usuario (username, password) VALUES ('enfermeiro', '$2a$10$N.zmdr9k7edaN1unU5z4/e8aKj024.sCg28V222Ym6S2p1/.b2i.G');
-- Paciente cujo username é o ID do paciente no sistema (ex: Paciente com id=1)
INSERT INTO usuario (username, password) VALUES ('1', '$2a$10$N.zmdr9k7edaN1unU5z4/e8aKj024.sCg28V222Ym6S2p1/.b2i.G');

-- Inserindo os perfis (roles) na tabela 'usuario_roles'
-- Assumindo que os IDs dos usuários são 1, 2 e 3 respectivamente.
INSERT INTO usuario_roles (usuario_id, roles) VALUES (1, 'MEDICO');
INSERT INTO usuario_roles (usuario_id, roles) VALUES (2, 'ENFERMEIRO');
INSERT INTO usuario_roles (usuario_id, roles) VALUES (3, 'PACIENTE');

-- Você também pode querer inserir um paciente de teste na tabela 'paciente' com ID = 1
INSERT INTO paciente (id, nome, cpf, data_nascimento, ativo, data_criacao) VALUES (1, 'Paciente Teste', '11122233344', '1980-01-01', true, CURRENT_TIMESTAMP);
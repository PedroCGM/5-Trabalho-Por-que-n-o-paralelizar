-- Inserting users
INSERT INTO usuario (login, senha)
VALUES ('user1', 'pass1'), ('user2', 'pass2');

-- Inserting products
INSERT INTO produto (nome, quantidade, precoVenda)
VALUES ('Apple', 150, 3.50), ('Orange', 300, 1.75), ('Grape', 600, 2.75);

-- Inserting people
INSERT INTO pessoa (nome, endereco, cidade, estado, telefone, email)
VALUES
('Carlos Silva', 'Rua das Flores', 'São Paulo', 'SP', '11999998888', 'carlos.silva@example.com'),
('Maria Oliveira', 'Avenida Central', 'Belo Horizonte', 'MG', '31988887777', 'maria.oliveira@example.com'),
('BUSINESS', 'Praça da Sé', 'Porto Alegre', 'RS', '51977776666', 'business@example.com');

-- Inserting physical person details
INSERT INTO pessoa_fisica (id_pessoa, cpf)
VALUES (1, '12345678900'), (2, '98765432100');

-- Inserting legal entity details
INSERT INTO pessoa_juridica (id_pessoa, cnpj)
VALUES (3, '55667788000199');

-- Inserting movements
INSERT INTO movimento (id_pessoa, id_produto, id_usuario, quantidade, tipo, valor_unitario)
VALUES
(1, 1, 1, 15, 'E', 10.00), 
(2, 2, 2, 8, 'S', 20.00);

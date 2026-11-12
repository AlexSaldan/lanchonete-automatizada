-- V3: Completa o esquema relacional da lanchonete.
-- Esta migração será validada primeiro no banco de laboratório.

-- 1. Completar a tabela mesas.
ALTER TABLE mesas
    MODIFY COLUMN capacidade INT NOT NULL,
    ADD COLUMN token_qr_code VARCHAR(36) NOT NULL UNIQUE;

-- 2. Ajustar a tabela produtos.
ALTER TABLE produtos
    MODIFY COLUMN disponivel BOOLEAN NOT NULL DEFAULT TRUE;

-- 3. Criar sessões de mesa.
CREATE TABLE sessoes_mesa (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    mesa_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    data_abertura DATETIME(6) NOT NULL,
    data_encerramento DATETIME(6),

    CONSTRAINT fk_sessoes_mesa_mesa
        FOREIGN KEY (mesa_id) REFERENCES mesas(id)
);

-- 4. Criar subcomandas.
CREATE TABLE subcomandas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sessao_mesa_id BIGINT NOT NULL,
    nome_cliente VARCHAR(100) NOT NULL,
    cpf_cliente VARCHAR(14),
    status_pagamento VARCHAR(20) NOT NULL,
    valor_total DECIMAL(10,2) NOT NULL,

    CONSTRAINT fk_subcomandas_sessao
        FOREIGN KEY (sessao_mesa_id) REFERENCES sessoes_mesa(id)
);

-- 5. Criar itens de pedido.
CREATE TABLE itens_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    subcomanda_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10,2) NOT NULL,
    observacao VARCHAR(255),
    status_preparo VARCHAR(20) NOT NULL,
    data_hora_pedido DATETIME(6) NOT NULL,

    CONSTRAINT fk_itens_pedido_subcomanda
        FOREIGN KEY (subcomanda_id) REFERENCES subcomandas(id),

    CONSTRAINT fk_itens_pedido_produto
        FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

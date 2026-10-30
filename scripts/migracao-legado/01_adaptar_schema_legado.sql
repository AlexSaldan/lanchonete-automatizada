-- Adaptacao do esquema legado para compatibilidade com Flyway V1-V3.
-- Executar somente em uma copia restaurada do banco legado.
-- Nao executar novamente em um banco ja adaptado.
-- Nao altera o historico do Flyway.

-- 1. Conversao dos tipos ENUM.
ALTER TABLE mesas
    MODIFY COLUMN status VARCHAR(20) NOT NULL;

ALTER TABLE produtos
    MODIFY COLUMN categoria VARCHAR(30) NOT NULL;

ALTER TABLE sessoes_mesa
    MODIFY COLUMN status VARCHAR(20) NOT NULL;

ALTER TABLE subcomandas
    MODIFY COLUMN status_pagamento VARCHAR(20) NOT NULL;

ALTER TABLE itens_pedido
    MODIFY COLUMN status_preparo VARCHAR(20) NOT NULL;

-- 2. Conversao da disponibilidade.
ALTER TABLE produtos
    MODIFY COLUMN disponivel TINYINT(1) NOT NULL DEFAULT 1;

-- 3. Colunas de auditoria.
-- Primeiro adicionamos como NULL para nao inventar datas historicas.
ALTER TABLE mesas
    ADD COLUMN data_criacao TIMESTAMP NULL DEFAULT NULL,
    ADD COLUMN data_atualizacao TIMESTAMP NULL DEFAULT NULL;

ALTER TABLE produtos
    ADD COLUMN data_criacao TIMESTAMP NULL DEFAULT NULL,
    ADD COLUMN data_atualizacao TIMESTAMP NULL DEFAULT NULL;

-- 4. Configuracao dos valores padrao para novos registros.
ALTER TABLE mesas
    ALTER COLUMN data_criacao SET DEFAULT (CURRENT_TIMESTAMP);

ALTER TABLE mesas
    MODIFY COLUMN data_atualizacao TIMESTAMP NULL
    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

ALTER TABLE produtos
    ALTER COLUMN data_criacao SET DEFAULT (CURRENT_TIMESTAMP);

ALTER TABLE produtos
    MODIFY COLUMN data_atualizacao TIMESTAMP NULL
    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- 5. Indices presentes no esquema Flyway.
CREATE INDEX idx_mesas_status ON mesas(status);

CREATE INDEX idx_produtos_categoria ON produtos(categoria);

CREATE INDEX idx_produtos_disponivel ON produtos(disponivel);

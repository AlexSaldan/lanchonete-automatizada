-- Migração V1: Criação da tabela de Mesas
-- Responsável por estruturar o armazenamento físico das mesas da lanchonete.

CREATE TABLE mesas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    capacidade INT,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Índice para melhorar a performance de buscas por status (ex: buscar mesas livres)
CREATE INDEX idx_mesas_status ON mesas(status);
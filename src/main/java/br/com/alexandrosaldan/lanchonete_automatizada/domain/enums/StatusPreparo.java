package br.com.alexandrosaldan.lanchonete_automatizada.domain.enums;

/**
 * Representa o ciclo de vida de preparo e entrega de um item na cozinha/bar.
 */
public enum StatusPreparo {
    RECEBIDO,
    EM_PREPARO,
    PRONTO,
    ENTREGUE,
    CANCELADO
}

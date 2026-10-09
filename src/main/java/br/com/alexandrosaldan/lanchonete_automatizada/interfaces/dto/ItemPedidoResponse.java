package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPreparo;
import java.math.BigDecimal;

public record ItemPedidoResponse(
    Long id,
    String nomeProduto,
    Integer quantidade,
    BigDecimal precoUnitario,
    BigDecimal subtotal,
    String observacao,
    StatusPreparo statusPreparo
) {}

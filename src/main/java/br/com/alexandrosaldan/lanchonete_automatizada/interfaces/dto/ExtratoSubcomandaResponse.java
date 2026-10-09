package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPagamento;
import java.math.BigDecimal;
import java.util.List;

public record ExtratoSubcomandaResponse(
    Long subcomandaId,
    String nomeCliente,
    StatusPagamento statusPagamento,
    BigDecimal valorTotal,
    List<ItemPedidoResponse> itens
) {}

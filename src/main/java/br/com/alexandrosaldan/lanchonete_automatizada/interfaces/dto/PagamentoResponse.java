package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPagamento;

import java.math.BigDecimal;

public record PagamentoResponse(
        Long subcomandaId,
        String nomeCliente,
        BigDecimal valorTotal,
        StatusPagamento statusPagamento
) {

    public static PagamentoResponse fromEntity(Subcomanda subcomanda) {
        return new PagamentoResponse(
                subcomanda.getId(),
                subcomanda.getNomeCliente(),
                subcomanda.getValorTotal(),
                subcomanda.getStatusPagamento()
        );
    }
}

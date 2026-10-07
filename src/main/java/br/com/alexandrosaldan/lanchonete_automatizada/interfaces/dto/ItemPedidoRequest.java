package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemPedidoRequest(
    @NotNull(message = "O ID da subcomanda é obrigatório")
    Long subcomandaId,

    @NotNull(message = "O ID do produto é obrigatório")
    Long produtoId,

    @NotNull(message = "A quantidade é obrigatória")
    @Positive(message = "A quantidade deve ser maior que zero")
    Integer quantidade,

    String observacao
) {}

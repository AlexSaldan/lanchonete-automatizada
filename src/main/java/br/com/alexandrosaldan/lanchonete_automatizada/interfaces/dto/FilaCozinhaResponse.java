package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto;

import java.time.LocalDateTime;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPreparo;

public record FilaCozinhaResponse(

        Long itemId,
        String nomeProduto,
        Integer quantidade,
        String observacao,
        StatusPreparo statusPreparo,
        LocalDateTime dataHoraPedido

) {
}

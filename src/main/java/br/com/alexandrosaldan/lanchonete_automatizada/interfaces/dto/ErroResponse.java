package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ErroResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    List<String> messages
) {
}

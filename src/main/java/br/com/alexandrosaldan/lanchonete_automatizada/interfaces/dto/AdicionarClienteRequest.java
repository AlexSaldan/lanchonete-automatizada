package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdicionarClienteRequest(
    @NotNull(message = "O ID da sessão é obrigatório")
    Long sessaoId,

    @NotBlank(message = "O nome do cliente é obrigatório")
    String nomeCliente,

    String cpfCliente
) {}

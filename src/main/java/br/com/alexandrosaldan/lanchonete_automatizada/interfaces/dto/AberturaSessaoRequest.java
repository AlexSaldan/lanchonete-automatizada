package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AberturaSessaoRequest(
    @NotNull(message = "O número da mesa é obrigatório")
    Integer numeroMesa,

    @NotBlank(message = "O token do QR Code é obrigatório")
    String tokenQrCode,

    @NotBlank(message = "O nome do primeiro cliente é obrigatório")
    String nomeCliente,

    String cpfCliente
) {}

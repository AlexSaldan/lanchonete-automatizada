package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.CategoriaProduto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * DTO para receber dados de criação de produtos.
 * As anotações do Bean Validation garantem a integridade antes de tocar no Service.
 */
public record ProdutoRequest(
    @NotBlank(message = "O nome do produto é obrigatório")
    String nome,
    
    String descricao,
    
    @NotNull(message = "A categoria é obrigatória")
    CategoriaProduto categoria,
    
    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    BigDecimal preco,
    
    @NotNull(message = "O tempo de preparo é obrigatório")
    @PositiveOrZero(message = "O tempo de preparo não pode ser negativo")
    Integer tempoPreparoMinutos
) {}

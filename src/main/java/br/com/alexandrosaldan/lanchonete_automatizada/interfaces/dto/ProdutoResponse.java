package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Produto;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.CategoriaProduto;
import java.math.BigDecimal;

/**
 * DTO para resposta da API, desacoplando a Entity do mundo externo.
 * Uso de 'record' (Java 14+) para imutabilidade e código limpo.
 */
public record ProdutoResponse(
    Long id,
    String nome,
    String descricao,
    CategoriaProduto categoria,
    BigDecimal preco,
    Integer tempoPreparoMinutos,
    Boolean disponivel
) {
    // Método estático para mapear a Entity para o DTO facilmente
    public static ProdutoResponse fromEntity(Produto produto) {
        return new ProdutoResponse(
            produto.getId(),
            produto.getNome(),
            produto.getDescricao(),
            produto.getCategoria(),
            produto.getPreco(),
            produto.getTempoPreparoMinutos(),
            produto.getDisponivel()
        );
    }
}

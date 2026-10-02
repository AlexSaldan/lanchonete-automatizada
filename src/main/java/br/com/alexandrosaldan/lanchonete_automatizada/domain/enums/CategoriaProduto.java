package br.com.alexandrosaldan.lanchonete_automatizada.domain.enums;

/**
 * Categorias de produtos da lanchonete.
 * A separação entre bebidas alcoólicas e não alcoólicas é crucial 
 * para validação de idade e regras de negócio futuras.
 */
public enum CategoriaProduto {
    LANCHE,
    PIZZA,
    PORCAO,
    BEBIDA_ALCOOLICA,
    BEBIDA_SEM_ALCOOL,
    SOBREMESA
}

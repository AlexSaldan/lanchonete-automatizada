package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Produto;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.CategoriaProduto;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.ProdutoNaoEncontradoException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Serviço responsável pelas regras de negócio de Produtos.
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    /**
     * Cria um novo produto após validar regras de negócio.
     */
    @Transactional
    public Produto criarProduto(Produto produto) {
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço do produto deve ser maior que zero.");
        }
        if (produto.getTempoPreparoMinutos() == null || produto.getTempoPreparoMinutos() <= 0) {
            throw new IllegalArgumentException("O tempo de preparo deve ser maior que zero.");
        }
        return produtoRepository.save(produto);
    }

    /**
     * Busca um produto pelo ID.
     */
    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto com ID " + id + " não encontrado."));
    }

    /**
     * Lista todos os produtos de uma categoria específica, ordenados por nome.
     */
    @Transactional(readOnly = true)
    public List<Produto> listarPorCategoria(CategoriaProduto categoria) {
        return produtoRepository.findByCategoriaOrderByNomeAsc(categoria);
    }
}

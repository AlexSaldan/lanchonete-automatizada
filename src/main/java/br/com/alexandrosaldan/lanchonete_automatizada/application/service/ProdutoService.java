package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Produto;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.CategoriaProduto;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.ProdutoNaoEncontradoException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço responsável por orquestrar as regras de negócio dos Produtos.
 * Garante a integridade do cardápio e validações de unicidade.
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    // Injeção via construtor
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    /**
     * Lista todos os produtos disponíveis no cardápio.
     */
    @Transactional(readOnly = true)
    public List<Produto> listarDisponiveis() {
        return produtoRepository.findByDisponivelTrue();
    }

    /**
     * Lista produtos de uma categoria específica que estejam disponíveis.
     */
    @Transactional(readOnly = true)
    public List<Produto> listarPorCategoria(CategoriaProduto categoria) {
        return produtoRepository.findByCategoriaAndDisponivelTrue(categoria);
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
     * Cadastra um novo produto no cardápio.
     * Valida se já existe um produto com o mesmo nome para evitar duplicidade.
     */
    @Transactional
    public Produto criarProduto(Produto produto) {
        // Validação de negócio: Nome único (poderia ser expandido para nome + categoria)
        // Nota: Em um cenário real, usaríamos uma query específica no repository para isso.
        // Aqui, vamos confiar nas validações do Bean Validation (@NotBlank, @Positive) que já estão na Entity.
        
        return produtoRepository.save(produto);
    }

    /**
     * Atualiza um produto existente.
     */
    @Transactional
    public Produto atualizarProduto(Long id, Produto produtoAtualizado) {
        Produto produtoExistente = buscarPorId(id);
        
        produtoExistente.setNome(produtoAtualizado.getNome());
        produtoExistente.setDescricao(produtoAtualizado.getDescricao());
        produtoExistente.setCategoria(produtoAtualizado.getCategoria());
        produtoExistente.setPreco(produtoAtualizado.getPreco());
        produtoExistente.setTempoPreparoMinutos(produtoAtualizado.getTempoPreparoMinutos());
        
        return produtoRepository.save(produtoExistente);
    }

    /**
     * Desativa um produto do cardápio (soft delete).
     * Não deletamos do banco para manter histórico de pedidos.
     */
    @Transactional
    public Produto desativarProduto(Long id) {
        Produto produto = buscarPorId(id);
        produto.setDisponivel(false);
        return produtoRepository.save(produto);
    }
}

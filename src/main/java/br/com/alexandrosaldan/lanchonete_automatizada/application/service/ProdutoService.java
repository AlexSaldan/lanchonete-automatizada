package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Produto;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.ProdutoNaoEncontradoException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Serviço responsável por orquestrar as regras de negócio dos Produtos.
 * Garante a integridade do cardápio e operações seguras de persistência.
 */
@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    // Injeção via construtor: prática recomendada para imutabilidade e testes
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    /**
     * Lista todos os produtos disponíveis para o cardápio.
     */
    @Transactional(readOnly = true)
    public List<Produto> listarDisponiveis() {
        return produtoRepository.findByDisponivelTrue();
    }

    /**
     * Busca um produto específico pelo seu identificador.
     */
    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new ProdutoNaoEncontradoException("Produto com ID " + id + " não encontrado."));
    }

    /**
     * Cadastra um novo produto no sistema.
     */
    @Transactional
    public Produto criarProduto(Produto produto) {
        // Validações de negócio complexas entram aqui no futuro (ex: nome único por categoria)
        return produtoRepository.save(produto);
    }

    /**
     * Desativa um produto do cardápio (Soft Delete).
     * Preserva o histórico de pedidos que já utilizaram este produto.
     */
    @Transactional
    public Produto desativarProduto(Long id) {
        Produto produto = buscarPorId(id);
        produto.setDisponivel(false);
        return produtoRepository.save(produto);
    }
}

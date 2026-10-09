package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.controller;

import br.com.alexandrosaldan.lanchonete_automatizada.application.service.ProdutoService;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Produto;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.ProdutoRequest;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.ProdutoResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * Controller REST responsável pelo gerenciamento e consulta do Cardápio / Produtos.
 * 
 * Arquitetura e Segurança:
 * - Camada de entrada desacoplada da camada de domínio através de DTOs (ProdutoRequest/ProdutoResponse).
 * - Sanitização e validação de entrada garantidas pelo Bean Validation (@Valid).
 * - Utilização de SLF4J para rastreamento de logs operacionais sem expor dados sensíveis.
 */
@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private static final Logger log = LoggerFactory.getLogger(ProdutoController.class);

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    /**
     * Lista todos os produtos que estão marcados como disponíveis para exibição no totem/cardápio digital.
     *
     * @return Lista de DTOs de resposta de produtos disponíveis (HTTP 200 OK)
     */
    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listarDisponiveis() {
        log.debug("Iniciando consulta de produtos disponíveis para exibição no cardápio");
        
        List<Produto> produtos = produtoService.listarDisponiveis();
        List<ProdutoResponse> response = produtos.stream()
                .map(ProdutoResponse::fromEntity)
                .toList();
                
        log.info("Consulta realizada com sucesso. Total de produtos disponíveis retornados: {}", response.size());
        return ResponseEntity.ok(response);
    }
     
/**
 * Consulta um produto pelo identificador.
 *
 * @param id identificador do produto
 * @return dados do produto encontrado
 */
@GetMapping("/{id}")
public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
    log.debug("Consultando produto com ID: {}", id);

    Produto produto = produtoService.buscarPorId(id);

    return ResponseEntity.ok(ProdutoResponse.fromEntity(produto));
}

/**
 * Desativa um produto do cardápio.
 *
 * @param id identificador do produto
 * @return HTTP 204 No Content
 */
@PatchMapping("/{id}/disponivel")
public ResponseEntity<Void> desativarProduto(@PathVariable Long id) {
    log.info("Solicitada desativação do produto com ID: {}", id);

    produtoService.desativarProduto(id);

    return ResponseEntity.noContent().build();
}
    /**
     * Cadastra um novo produto no cardápio do sistema.
     *
     * @param request DTO validado contendo os dados de criação do produto
     * @return DTO com o produto criado e cabeçalho Location apontando para o novo recurso (HTTP 201 Created)
     */
    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
        log.info("Recebida requisição para cadastrar novo produto: '{}' da categoria '{}'", request.nome(), request.categoria());
        
        // Converte DTO de requisição para Entidade de Domínio
        Produto novoProduto = new Produto(
            request.nome(),
            request.descricao(),
            request.categoria(),
            request.preco(),
            request.tempoPreparoMinutos()
        );
        
        // Processa a regra de negócio na camada de serviço
        Produto produtoSalvo = produtoService.criarProduto(novoProduto);
        log.info("Produto salvo com sucesso no banco de dados. ID gerado: {}", produtoSalvo.getId());
        
        // Mapeia para DTO de resposta imutável
        ProdutoResponse responseDto = ProdutoResponse.fromEntity(produtoSalvo);
        
        // Constrói a URI do recurso criado
        URI location = URI.create("/produtos/" + produtoSalvo.getId());
        
        return ResponseEntity.created(location).body(responseDto);
    }
}

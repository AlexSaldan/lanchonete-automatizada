package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.controller;

import br.com.alexandrosaldan.lanchonete_automatizada.application.service.ProdutoService;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Produto;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.ProdutoRequest;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.ProdutoResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponse>> listarDisponiveis() {
        List<Produto> produtos = produtoService.listarDisponiveis();
        List<ProdutoResponse> response = produtos.stream()
                .map(ProdutoResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponse> buscarPorId(@PathVariable Long id) {
        Produto produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(ProdutoResponse.fromEntity(produto));
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
        Produto novoProduto = new Produto(
            request.nome(),
            request.descricao(),
            request.categoria(),
            request.preco(),
            request.tempoPreparoMinutos()
        );
        
        Produto produtoSalvo = produtoService.criarProduto(novoProduto);
        
        return ResponseEntity.created(URI.create("/produtos/" + produtoSalvo.getId()))
                             .body(ProdutoResponse.fromEntity(produtoSalvo));
    }

    @PatchMapping("/{id}/disponivel")
    public ResponseEntity<Void> desativarProduto(@PathVariable Long id) {
        produtoService.desativarProduto(id);
        return ResponseEntity.noContent().build();
    }
}

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

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
        // 1. Converte o DTO de entrada para a Entity de domínio
        Produto novoProduto = new Produto(
            request.nome(),
            request.descricao(),
            request.categoria(),
            request.preco(),
            request.tempoPreparoMinutos()
        );
        
        // 2. Delega a regra de negócio para o Service
        Produto produtoSalvo = produtoService.criarProduto(novoProduto);
        
        // 3. Retorna a resposta com o código HTTP 201 Created e o URI do novo recurso
        return ResponseEntity.created(URI.create("/produtos/" + produtoSalvo.getId()))
                             .body(ProdutoResponse.fromEntity(produtoSalvo));
    }
}

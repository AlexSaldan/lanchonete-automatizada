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
        System.out.println(">>> [DEBUG GET] Iniciando listagem...");
        List<Produto> produtos = produtoService.listarDisponiveis();
        List<ProdutoResponse> response = produtos.stream()
                .map(ProdutoResponse::fromEntity)
                .toList();
        System.out.println(">>> [DEBUG GET] Sucesso! Retornando " + response.size() + " produtos.");
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest request) {
        System.out.println(">>> [DEBUG POST 1] Request recebido: " + request);
        
        // 1. Converte o DTO de entrada para a Entity de domínio
        Produto novoProduto = new Produto(
            request.nome(),
            request.descricao(),
            request.categoria(),
            request.preco(),
            request.tempoPreparoMinutos()
        );
        System.out.println(">>> [DEBUG POST 2] Entity criada. Chamando service para salvar...");
        
        // 2. Delega a regra de negócio para o Service
        Produto produtoSalvo = produtoService.criarProduto(novoProduto);
        System.out.println(">>> [DEBUG POST 3] Service retornou! ID do produto salvo: " + produtoSalvo.getId());
        
        // 3. Converte para DTO de resposta
        ProdutoResponse responseDto = ProdutoResponse.fromEntity(produtoSalvo);
        System.out.println(">>> [DEBUG POST 4] DTO de resposta criado com sucesso.");
        
        // 4. Monta e retorna a resposta HTTP
        ResponseEntity<ProdutoResponse> response = ResponseEntity.created(URI.create("/produtos/" + produtoSalvo.getId()))
                             .body(responseDto);
                             
        System.out.println(">>> [DEBUG POST 5] Resposta HTTP montada. Finalizando método.");
        return response;
    }
}

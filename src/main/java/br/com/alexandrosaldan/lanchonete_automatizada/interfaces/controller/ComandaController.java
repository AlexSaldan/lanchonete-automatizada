package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.controller;

import br.com.alexandrosaldan.lanchonete_automatizada.application.service.ComandaService;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * Controller REST responsável pelo gerenciamento de Comandas Compartilhadas.
 *
 * Fluxo de Negócio:
 * 1. Cliente escaneia QR Code → abreSessao()
 * 2. Novos clientes se juntam → adicionarCliente()
 * 3. Clientes fazem pedidos → lancarItem()
 * 4. Visualização do extrato → buscarExtratoMesa()
 *
 * Arquitetura:
 * - Desacoplamento total via DTOs (Request/Response)
 * - Validação de entrada com Bean Validation (@Valid)
 * - Logs operacionais com SLF4J para auditoria
 */
@RestController
@RequestMapping("/comandas")
public class ComandaController {

    private static final Logger log = LoggerFactory.getLogger(ComandaController.class);

    private final ComandaService comandaService;

    public ComandaController(ComandaService comandaService) {
        this.comandaService = comandaService;
    }

    /**
     * Abre uma nova sessão na mesa (primeiro cliente).
     * Valida o token do QR Code para garantir que o cliente está fisicamente na mesa.
     *
     * @param request DTO com número da mesa, token QR Code e dados do primeiro cliente
     * @return Extrato completo da mesa com a sessão aberta (HTTP 201 Created)
     */
    @PostMapping("/sessao/abrir")
    public ResponseEntity<ExtratoMesaResponse> abrirSessao(@Valid @RequestBody AberturaSessaoRequest request) {
        log.info("Recebida requisição para abrir sessão na mesa {} pelo cliente '{}'",
                request.numeroMesa(), request.nomeCliente());

        ExtratoMesaResponse response = comandaService.abrirSessao(request);

        log.info("Sessão aberta com sucesso. ID da sessão: {}", response.sessaoId());

        URI location = URI.create("/comandas/sessao/" + response.sessaoId());
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Adiciona um novo cliente (subcomanda) a uma sessão existente.
     * Permite o split de contas (múltiplos clientes pagando individualmente na mesma mesa).
     *
     * @param request DTO com ID da sessão e dados do novo cliente
     * @return Dados da subcomanda criada (HTTP 201 Created)
     */
    @PostMapping("/subcomanda/adicionar")
    public ResponseEntity<ExtratoSubcomandaResponse> adicionarCliente(@Valid @RequestBody AdicionarClienteRequest request) {
        log.info("Recebida requisição para adicionar cliente '{}' à sessão {}",
                request.nomeCliente(), request.sessaoId());

        ExtratoSubcomandaResponse response = comandaService.adicionarCliente(request);

        log.info("Cliente adicionado com sucesso. ID da subcomanda: {}", response.subcomandaId());

        URI location = URI.create("/comandas/subcomanda/" + response.subcomandaId());
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Lança um item de produto na subcomanda do cliente.
     * O preço é capturado no momento do pedido (snapshot) para garantir integridade histórica.
     *
     * @param request DTO com IDs da subcomanda e produto, quantidade e observação
     * @return Dados do item lançado (HTTP 201 Created)
     */
    @PostMapping("/item/lancar")
    public ResponseEntity<ItemPedidoResponse> lancarItem(@Valid @RequestBody ItemPedidoRequest request) {
        log.info("Recebida requisição para lançar produto ID {} na subcomanda ID {} (qtd: {})",
                request.produtoId(), request.subcomandaId(), request.quantidade());

        ItemPedidoResponse response = comandaService.lancarItem(request);

        log.info("Item lançado com sucesso. ID do item: {}, Subtotal: R$ {}",
                response.id(), response.subtotal());

        URI location = URI.create("/comandas/item/" + response.id());
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Busca o extrato completo da mesa (visão geral + detalhamento por subcomanda).
     * Permite que cada cliente visualize sua conta individual e o total da mesa.
     *
     * @param sessaoId ID da sessão/comanda geral
     * @return Extrato completo da mesa (HTTP 200 OK)
     */
    @GetMapping("/sessao/{sessaoId}/extrato")
    public ResponseEntity<ExtratoMesaResponse> buscarExtratoMesa(@PathVariable Long sessaoId) {
        log.debug("Consultando extrato da sessão ID {}", sessaoId);

        ExtratoMesaResponse response = comandaService.buscarExtratoMesa(sessaoId);

        log.info("Extrato retornado com sucesso. Mesa: {}, Total: R$ {}, Subcomandas: {}",
                response.numeroMesa(), response.valorTotalMesa(), response.subcomandas().size());

        return ResponseEntity.ok(response);
    }
}




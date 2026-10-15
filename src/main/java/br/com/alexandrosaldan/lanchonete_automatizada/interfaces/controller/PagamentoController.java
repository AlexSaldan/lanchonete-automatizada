package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.controller;

import br.com.alexandrosaldan.lanchonete_automatizada.application.service.PagamentoService;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.PagamentoResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pagamentos/subcomandas/{subcomandaId}")
public class PagamentoController {

    private static final Logger log = LoggerFactory.getLogger(PagamentoController.class);

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @GetMapping
    public ResponseEntity<PagamentoResponse> consultarPagamento(
            @PathVariable Long subcomandaId) {

        Subcomanda subcomanda = pagamentoService.consultarSubcomanda(subcomandaId);

        return ResponseEntity.ok(PagamentoResponse.fromEntity(subcomanda));
    }

    @PatchMapping("/iniciar")
    public ResponseEntity<PagamentoResponse> iniciarPagamento(
            @PathVariable Long subcomandaId) {

        log.info("Solicitação para iniciar pagamento da subcomanda ID {}", subcomandaId);

        Subcomanda subcomanda = pagamentoService.iniciarPagamento(subcomandaId);

        log.info("Pagamento iniciado para a subcomanda ID {}", subcomandaId);

        return ResponseEntity.ok(PagamentoResponse.fromEntity(subcomanda));
    }

    @PatchMapping("/confirmar")
    public ResponseEntity<PagamentoResponse> confirmarPagamento(
            @PathVariable Long subcomandaId) {

        log.info("Solicitação para confirmar pagamento da subcomanda ID {}", subcomandaId);

        Subcomanda subcomanda = pagamentoService.confirmarPagamento(subcomandaId);

        log.info("Pagamento confirmado para a subcomanda ID {}", subcomandaId);

        return ResponseEntity.ok(PagamentoResponse.fromEntity(subcomanda));
    }
}

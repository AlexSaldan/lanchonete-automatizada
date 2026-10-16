package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.controller;

import br.com.alexandrosaldan.lanchonete_automatizada.application.service.PagamentoService;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.PagamentoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.ErroResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Tag(
	    name = "Pagamentos",
	    description = "Consulta, início e confirmação simulada de pagamentos das subcomandas"
	)
@RestController
@RequestMapping("/pagamentos/subcomandas/{subcomandaId}")
public class PagamentoController {

    private static final Logger log = LoggerFactory.getLogger(PagamentoController.class);

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }
    @Operation(
	    summary = "Consultar pagamento da subcomanda",
	    description = "Consulta os dados e o status atual do pagamento de uma subcomanda."
	)
    @ApiResponses(value = {
	    @ApiResponse(
	        responseCode = "200",
	        description = "Pagamento consultado com sucesso"
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Subcomanda não encontrada",
	        content = @Content(
	            schema = @Schema(implementation = ErroResponse.class)
	        )
	    )
	})
    @GetMapping
    public ResponseEntity<PagamentoResponse> consultarPagamento(
            @PathVariable Long subcomandaId) {

        Subcomanda subcomanda = pagamentoService.consultarSubcomanda(subcomandaId);

        return ResponseEntity.ok(PagamentoResponse.fromEntity(subcomanda));
    }

    @Operation(
	    summary = "Iniciar pagamento da subcomanda",
	    description = "Inicia o pagamento simulado de uma subcomanda, alterando seu status de PENDENTE para PROCESSANDO. A subcomanda deve possuir valor maior que zero."
	)
    @ApiResponses(value = {
	    @ApiResponse(
	        responseCode = "200",
	        description = "Pagamento iniciado com sucesso"
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Subcomanda não encontrada",
	        content = @Content(
	            schema = @Schema(implementation = ErroResponse.class)
	        )
	    ),
	    @ApiResponse(
	        responseCode = "409",
	        description = "Pagamento não está pendente ou subcomanda não possui valor positivo",
	        content = @Content(
	            schema = @Schema(implementation = ErroResponse.class)
	        )
	    )
	})
    @PatchMapping("/iniciar")
    public ResponseEntity<PagamentoResponse> iniciarPagamento(
            @PathVariable Long subcomandaId) {

        log.info("Solicitação para iniciar pagamento da subcomanda ID {}", subcomandaId);

        Subcomanda subcomanda = pagamentoService.iniciarPagamento(subcomandaId);

        log.info("Pagamento iniciado para a subcomanda ID {}", subcomandaId);

        return ResponseEntity.ok(PagamentoResponse.fromEntity(subcomanda));
    }

    @Operation(
	    summary = "Confirmar pagamento da subcomanda",
	    description = "Confirma administrativamente um pagamento simulado, alterando o status de PROCESSANDO para PAGO. Não representa uma confirmação bancária real."
	)
    @ApiResponses(value = {
	    @ApiResponse(
	        responseCode = "200",
	        description = "Pagamento simulado confirmado com sucesso"
	    ),
	    @ApiResponse(
	        responseCode = "404",
	        description = "Subcomanda não encontrada",
	        content = @Content(
	            schema = @Schema(implementation = ErroResponse.class)
	        )
	    ),
	    @ApiResponse(
	        responseCode = "409",
	        description = "Pagamento não está em processamento",
	        content = @Content(
	            schema = @Schema(implementation = ErroResponse.class)
	        )
	    )
	})
    @PatchMapping("/confirmar")
    public ResponseEntity<PagamentoResponse> confirmarPagamento(
            @PathVariable Long subcomandaId) {

        log.info("Solicitação para confirmar pagamento da subcomanda ID {}", subcomandaId);

        Subcomanda subcomanda = pagamentoService.confirmarPagamento(subcomandaId);

        log.info("Pagamento confirmado para a subcomanda ID {}", subcomandaId);

        return ResponseEntity.ok(PagamentoResponse.fromEntity(subcomanda));
    }
}

package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.controller;

import br.com.alexandrosaldan.lanchonete_automatizada.application.service.MesaService;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Mesa;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.ErroResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
    name = "Mesas",
    description = "Consulta e gerenciamento da ocupação das mesas da lanchonete"
)
@RestController
@RequestMapping("/mesas/{numero}")
public class MesaController {

    private final MesaService mesaService;

    // Injeção de dependência via construtor
    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    // =====================================================
    // GET - BUSCAR MESA POR NÚMERO
    // =====================================================

    @Operation(
        summary = "Buscar mesa por número",
        description = "Consulta os dados de uma mesa pelo seu número."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Mesa encontrada com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mesa não encontrada",
            content = @Content(
                schema = @Schema(implementation = ErroResponse.class)
            )
        )
    })
    @GetMapping
    public ResponseEntity<Mesa> buscarPorNumero(
            @PathVariable Integer numero) {

        Mesa mesa = mesaService.buscarPorNumero(numero);

        return ResponseEntity.ok(mesa);
    }

    // =====================================================
    // PATCH - OCUPAR MESA
    // =====================================================

    @Operation(
        summary = "Ocupar mesa",
        description = "Altera o status de uma mesa para ocupada."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Mesa ocupada com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mesa não encontrada",
            content = @Content(
                schema = @Schema(implementation = ErroResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Mesa já ocupada",
            content = @Content(
                schema = @Schema(implementation = ErroResponse.class)
            )
        )
    })
    @PatchMapping("/ocupar")
    public ResponseEntity<Mesa> ocuparMesa(
            @PathVariable Integer numero) {

        Mesa mesaOcupada = mesaService.ocuparMesa(numero);

        return ResponseEntity.ok(mesaOcupada);
    }

    // =====================================================
    // PATCH - LIBERAR MESA
    // =====================================================

    @Operation(
        summary = "Liberar mesa",
        description = "Altera o status de uma mesa para disponível."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Mesa liberada com sucesso"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Mesa não encontrada",
            content = @Content(
                schema = @Schema(implementation = ErroResponse.class)
            )
        )
    })
    @PatchMapping("/liberar")
    public ResponseEntity<Mesa> liberarMesa(
            @PathVariable Integer numero) {

        Mesa mesaLiberada = mesaService.liberarMesa(numero);

        return ResponseEntity.ok(mesaLiberada);
    }
}

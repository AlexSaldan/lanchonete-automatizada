package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.controller;

import br.com.alexandrosaldan.lanchonete_automatizada.application.service.CozinhaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.FilaCozinhaResponse;

@Tag(
    name = "Cozinha",
    description = "Gerenciamento do preparo e entrega dos pedidos"
)
@RestController
@RequestMapping("/cozinha")
public class CozinhaController {

    private static final Logger log =
            LoggerFactory.getLogger(CozinhaController.class);

    private final CozinhaService cozinhaService;

    public CozinhaController(CozinhaService cozinhaService) {
        this.cozinhaService = cozinhaService;
    }

    @Operation(summary = "Iniciar preparo de um pedido")
    @PatchMapping("/itens/{itemId}/iniciar")
    public ResponseEntity<Void> iniciarPreparo(@PathVariable Long itemId) {

        log.info("Iniciando preparo do item {}", itemId);

        cozinhaService.iniciarPreparo(itemId);

        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Marcar pedido como pronto")
    @PatchMapping("/itens/{itemId}/pronto")
    public ResponseEntity<Void> marcarComoPronto(@PathVariable Long itemId) {

        log.info("Marcando item {} como pronto", itemId);

        cozinhaService.marcarComoPronto(itemId);

        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Registrar entrega de um pedido")
    @PatchMapping("/itens/{itemId}/entregar")
    public ResponseEntity<Void> marcarComoEntregue(@PathVariable Long itemId) {

        log.info("Registrando entrega do item {}", itemId);

        cozinhaService.marcarComoEntregue(itemId);

        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Consultar fila de pedidos da cozinha")
    @GetMapping("/fila")
    public ResponseEntity<List<FilaCozinhaResponse>> consultarFila() {

        log.info("Consultando fila de pedidos da cozinha");

        List<FilaCozinhaResponse> fila = cozinhaService.consultarFila();

        return ResponseEntity.ok(fila);
    }

}

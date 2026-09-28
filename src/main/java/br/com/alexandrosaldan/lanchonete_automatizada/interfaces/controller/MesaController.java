package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.controller;

import br.com.alexandrosaldan.lanchonete_automatizada.application.service.MesaService;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Mesa;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mesas")
public class MesaController {

    private final MesaService mesaService;

    // Injeção de dependência via construtor (prática recomendada, facilita testes)
    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    @GetMapping("/{numero}")
    public ResponseEntity<Mesa> buscarPorNumero(@PathVariable Integer numero) {
        Mesa mesa = mesaService.buscarPorNumero(numero);
        return ResponseEntity.ok(mesa);
    }

    @PatchMapping("/{numero}/ocupar")
    public ResponseEntity<Mesa> ocuparMesa(@PathVariable Integer numero) {
        Mesa mesaOcupada = mesaService.ocuparMesa(numero);
        return ResponseEntity.ok(mesaOcupada);
    }

    @PatchMapping("/{numero}/liberar")
    public ResponseEntity<Mesa> liberarMesa(@PathVariable Integer numero) {
        Mesa mesaLiberada = mesaService.liberarMesa(numero);
        return ResponseEntity.ok(mesaLiberada);
    }
}

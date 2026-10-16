package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.controller;

import br.com.alexandrosaldan.lanchonete_automatizada.application.service.MesaService;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.exception.GlobalExceptionHandler;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.MesaNaoEncontradaException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Mesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusMesa;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MesaController.class)
@Import(GlobalExceptionHandler.class)
class MesaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MesaService mesaService;
    
    @Test
    void deveRetornar404QuandoMesaNaoExistir() throws Exception {

        when(mesaService.buscarPorNumero(99))
                .thenThrow(new MesaNaoEncontradaException(
                        "Mesa com número 99 não encontrada."));

        mockMvc.perform(get("/mesas/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Recurso Não Encontrado"))
                .andExpect(jsonPath("$.message")
                        .value("Mesa com número 99 não encontrada."));
    }
    @Test
    void deveRetornar200QuandoMesaExistir() throws Exception {

        Mesa mesa = new Mesa(1, 4);

        when(mesaService.buscarPorNumero(1))
                .thenReturn(mesa);

        mockMvc.perform(get("/mesas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(1))
                .andExpect(jsonPath("$.capacidade").value(4));
    }
    @Test
    void deveRetornar200AoOcuparMesa() throws Exception {

        Mesa mesa = new Mesa(1, 4);
        mesa.setStatus(StatusMesa.OCUPADA);

        when(mesaService.ocuparMesa(1))
                .thenReturn(mesa);

        mockMvc.perform(patch("/mesas/1/ocupar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(1))
                .andExpect(jsonPath("$.status").value("OCUPADA"));
    }
    @Test
    void deveRetornar409QuandoMesaJaEstiverOcupada() throws Exception {

        when(mesaService.ocuparMesa(1))
                .thenThrow(new IllegalStateException(
                        "A mesa 1 já está ocupada."));

        mockMvc.perform(patch("/mesas/1/ocupar"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error")
                        .value("Conflito de Regra de Negócio"))
                .andExpect(jsonPath("$.message")
                        .value("A mesa 1 já está ocupada."));
    }
    @Test
    void deveRetornar200AoLiberarMesa() throws Exception {

        Mesa mesa = new Mesa(1, 4);
        mesa.setStatus(StatusMesa.DISPONIVEL);

        when(mesaService.liberarMesa(1))
                .thenReturn(mesa);

        mockMvc.perform(patch("/mesas/1/liberar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value(1))
                .andExpect(jsonPath("$.status").value("DISPONIVEL"));
    }

}
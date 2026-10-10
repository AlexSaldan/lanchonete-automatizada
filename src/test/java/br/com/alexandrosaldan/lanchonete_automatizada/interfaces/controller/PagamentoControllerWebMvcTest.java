package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.controller;

import br.com.alexandrosaldan.lanchonete_automatizada.application.service.PagamentoService;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPagamento;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.SubcomandaNaoEncontradaException;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.exception.GlobalExceptionHandler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagamentoController.class)
@Import(GlobalExceptionHandler.class)
class PagamentoControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PagamentoService pagamentoService;

    @Test
    @DisplayName("Deve consultar pagamento existente com HTTP 200")
    void deveConsultarPagamentoComSucesso() throws Exception {
        Subcomanda subcomanda = criarSubcomanda(StatusPagamento.PENDENTE);

        when(pagamentoService.consultarSubcomanda(1L))
                .thenReturn(subcomanda);

        mockMvc.perform(get("/pagamentos/subcomandas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subcomandaId").value(1))
                .andExpect(jsonPath("$.nomeCliente").value("João"))
                .andExpect(jsonPath("$.valorTotal").value(45.80))
                .andExpect(jsonPath("$.statusPagamento").value("PENDENTE"))
                .andExpect(jsonPath("$.cpfCliente").doesNotExist());
    }

    @Test
    @DisplayName("Deve iniciar pagamento com HTTP 200")
    void deveIniciarPagamentoComSucesso() throws Exception {
        Subcomanda subcomanda = criarSubcomanda(StatusPagamento.PROCESSANDO);

        when(pagamentoService.iniciarPagamento(1L))
                .thenReturn(subcomanda);

        mockMvc.perform(patch("/pagamentos/subcomandas/1/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subcomandaId").value(1))
                .andExpect(jsonPath("$.valorTotal").value(45.80))
                .andExpect(jsonPath("$.statusPagamento").value("PROCESSANDO"));
    }

    @Test
    @DisplayName("Deve retornar HTTP 404 quando subcomanda não existir")
    void deveRetornar404QuandoSubcomandaNaoExistir() throws Exception {
        when(pagamentoService.consultarSubcomanda(999L))
                .thenThrow(new SubcomandaNaoEncontradaException(
                        "Subcomanda não encontrada: 999"
                ));

        mockMvc.perform(get("/pagamentos/subcomandas/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Subcomanda Não Encontrada"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("Deve retornar HTTP 409 quando pagamento estiver em estado inválido")
    void deveRetornar409QuandoPagamentoEstiverEmEstadoInvalido() throws Exception {
        when(pagamentoService.iniciarPagamento(1L))
                .thenThrow(new IllegalStateException(
                        "Somente subcomandas pendentes podem iniciar pagamento."
                ));

        mockMvc.perform(patch("/pagamentos/subcomandas/1/iniciar"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflito de Regra de Negócio"))
                .andExpect(jsonPath("$.message").value(
                        "Somente subcomandas pendentes podem iniciar pagamento."
                ));
    }

    private Subcomanda criarSubcomanda(StatusPagamento status) {
        Subcomanda subcomanda = new Subcomanda("João", null);
        subcomanda.setId(1L);
        subcomanda.setValorTotal(new BigDecimal("45.80"));
        subcomanda.setStatusPagamento(status);

        return subcomanda;
    }
}

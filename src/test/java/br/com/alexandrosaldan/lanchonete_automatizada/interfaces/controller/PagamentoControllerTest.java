package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.controller;

import br.com.alexandrosaldan.lanchonete_automatizada.application.service.PagamentoService;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPagamento;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.PagamentoResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoControllerTest {

    @Mock
    private PagamentoService pagamentoService;

    @InjectMocks
    private PagamentoController pagamentoController;

    private Subcomanda subcomanda;

    @BeforeEach
    void configurar() {
        subcomanda = new Subcomanda("João", null);
        subcomanda.setId(1L);
        subcomanda.setValorTotal(new BigDecimal("45.80"));
        subcomanda.setStatusPagamento(StatusPagamento.PENDENTE);
    }

    @Test
    void deveConsultarPagamentoComSucesso() {
        when(pagamentoService.consultarSubcomanda(1L))
                .thenReturn(subcomanda);

        ResponseEntity<PagamentoResponse> resposta =
                pagamentoController.consultarPagamento(1L);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());

        PagamentoResponse pagamento = resposta.getBody();

        assertEquals(1L, pagamento.subcomandaId());
        assertEquals("João", pagamento.nomeCliente());
        assertEquals(new BigDecimal("45.80"), pagamento.valorTotal());
        assertEquals(StatusPagamento.PENDENTE, pagamento.statusPagamento());

        verify(pagamentoService).consultarSubcomanda(1L);
    }

    @Test
    void deveIniciarPagamentoComSucesso() {
        subcomanda.setStatusPagamento(StatusPagamento.PROCESSANDO);

        when(pagamentoService.iniciarPagamento(1L))
                .thenReturn(subcomanda);

        ResponseEntity<PagamentoResponse> resposta =
                pagamentoController.iniciarPagamento(1L);

        assertEquals(HttpStatus.OK, resposta.getStatusCode());
        assertNotNull(resposta.getBody());

        PagamentoResponse pagamento = resposta.getBody();

        assertEquals(1L, pagamento.subcomandaId());
        assertEquals(StatusPagamento.PROCESSANDO, pagamento.statusPagamento());
        assertEquals(new BigDecimal("45.80"), pagamento.valorTotal());

        verify(pagamentoService).iniciarPagamento(1L);
    }

    @Test
    void devePropagarErroQuandoPagamentoNaoPodeSerIniciado() {
        when(pagamentoService.iniciarPagamento(1L))
                .thenThrow(new IllegalStateException(
                        "Somente subcomandas pendentes podem iniciar pagamento."
                ));

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> pagamentoController.iniciarPagamento(1L)
        );

        assertEquals(
                "Somente subcomandas pendentes podem iniciar pagamento.",
                excecao.getMessage()
        );

        verify(pagamentoService).iniciarPagamento(1L);
    }
}

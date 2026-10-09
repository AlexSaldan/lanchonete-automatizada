package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPagamento;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.SubcomandaNaoEncontradaException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.SubcomandaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

    @Mock
    private SubcomandaRepository subcomandaRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Subcomanda subcomanda;

    @BeforeEach
    void configurar() {
        subcomanda = new Subcomanda("João", null);
        subcomanda.setId(1L);
        subcomanda.setValorTotal(new BigDecimal("45.80"));
        subcomanda.setStatusPagamento(StatusPagamento.PENDENTE);
    }

    @Test
    void deveIniciarPagamentoPendente() {

        when(subcomandaRepository.findById(1L))
                .thenReturn(Optional.of(subcomanda));

        when(subcomandaRepository.save(any(Subcomanda.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Subcomanda resultado = pagamentoService.iniciarPagamento(1L);

        assertEquals(StatusPagamento.PROCESSANDO, resultado.getStatusPagamento());

        verify(subcomandaRepository).save(subcomanda);
    }

    @Test
    void naoDeveIniciarPagamentoJaPago() {

        subcomanda.setStatusPagamento(StatusPagamento.PAGO);

        when(subcomandaRepository.findById(1L))
                .thenReturn(Optional.of(subcomanda));

        assertThrows(
                IllegalStateException.class,
                () -> pagamentoService.iniciarPagamento(1L)
        );

        verify(subcomandaRepository, never()).save(any());
    }

    @Test
    void naoDeveIniciarPagamentoComValorZero() {

        subcomanda.setValorTotal(BigDecimal.ZERO);

        when(subcomandaRepository.findById(1L))
                .thenReturn(Optional.of(subcomanda));

        assertThrows(
                IllegalStateException.class,
                () -> pagamentoService.iniciarPagamento(1L)
        );

        verify(subcomandaRepository, never()).save(any());
    }

    @Test
    void deveConfirmarPagamentoEmProcessamento() {

        subcomanda.setStatusPagamento(StatusPagamento.PROCESSANDO);

        when(subcomandaRepository.findById(1L))
                .thenReturn(Optional.of(subcomanda));

        when(subcomandaRepository.save(any(Subcomanda.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Subcomanda resultado = pagamentoService.confirmarPagamento(1L);

        assertEquals(StatusPagamento.PAGO, resultado.getStatusPagamento());

        verify(subcomandaRepository).save(subcomanda);
    }

    @Test
    void naoDeveConfirmarPagamentoPendente() {

        when(subcomandaRepository.findById(1L))
                .thenReturn(Optional.of(subcomanda));

        assertThrows(
                IllegalStateException.class,
                () -> pagamentoService.confirmarPagamento(1L)
        );

        verify(subcomandaRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoSubcomandaNaoExiste() {

        when(subcomandaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                SubcomandaNaoEncontradaException.class,
                () -> pagamentoService.consultarSubcomanda(99L)
        );

        verify(subcomandaRepository, never()).save(any());
    }
}
package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.ItemPedido;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPreparo;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.ItemPedidoRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Produto;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.FilaCozinhaResponse;
import java.util.List;

import java.util.List;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CozinhaServiceTest {

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @InjectMocks
    private CozinhaService cozinhaService;

    @Test
    void deveIniciarPreparoDePedidoRecebido() {

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setStatusPreparo(StatusPreparo.RECEBIDO);

        when(itemPedidoRepository.findById(1L))
                .thenReturn(Optional.of(item));

        cozinhaService.iniciarPreparo(1L);

        assertEquals(
                StatusPreparo.EM_PREPARO,
                item.getStatusPreparo()
        );

        verify(itemPedidoRepository).save(item);
    }
    @Test
    void naoDeveIniciarPreparoDePedidoEntregue() {

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setStatusPreparo(StatusPreparo.ENTREGUE);

        when(itemPedidoRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertThrows(
                IllegalStateException.class,
                () -> cozinhaService.iniciarPreparo(1L)
        );

        assertEquals(
                StatusPreparo.ENTREGUE,
                item.getStatusPreparo()
        );

        verify(itemPedidoRepository, never()).save(any());
    }
    @Test
    void naoDeveIniciarPreparoQuandoPedidoNaoExiste() {

        when(itemPedidoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> cozinhaService.iniciarPreparo(99L)
        );

        verify(itemPedidoRepository, never()).save(any());
    }
    @Test
    void deveMarcarPedidoEmPreparoComoPronto() {

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setStatusPreparo(StatusPreparo.EM_PREPARO);

        when(itemPedidoRepository.findById(1L))
                .thenReturn(Optional.of(item));

        cozinhaService.marcarComoPronto(1L);

        assertEquals(
                StatusPreparo.PRONTO,
                item.getStatusPreparo()
        );

        verify(itemPedidoRepository).save(item);
    }
    @Test
    void naoDeveMarcarPedidoProntoNovamente() {

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setStatusPreparo(StatusPreparo.PRONTO);

        when(itemPedidoRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertThrows(
                IllegalStateException.class,
                () -> cozinhaService.marcarComoPronto(1L)
        );

        assertEquals(
                StatusPreparo.PRONTO,
                item.getStatusPreparo()
        );

        verify(itemPedidoRepository, never()).save(any());
    }
    @Test
    void deveEntregarPedidoPronto() {

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setStatusPreparo(StatusPreparo.PRONTO);

        when(itemPedidoRepository.findById(1L))
                .thenReturn(Optional.of(item));

        cozinhaService.marcarComoEntregue(1L);

        assertEquals(
                StatusPreparo.ENTREGUE,
                item.getStatusPreparo()
        );

        verify(itemPedidoRepository).save(item);
    }
    @Test
    void naoDeveEntregarPedidoEmPreparo() {

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setStatusPreparo(StatusPreparo.EM_PREPARO);

        when(itemPedidoRepository.findById(1L))
                .thenReturn(Optional.of(item));

        assertThrows(
                IllegalStateException.class,
                () -> cozinhaService.marcarComoEntregue(1L)
        );

        assertEquals(
                StatusPreparo.EM_PREPARO,
                item.getStatusPreparo()
        );

        verify(itemPedidoRepository, never()).save(any());
    }
    @Test
    void naoDeveEntregarPedidoInexistente() {

        when(itemPedidoRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> cozinhaService.marcarComoEntregue(99L)
        );

        verify(itemPedidoRepository, never()).save(any());
    }
    @Test
    void deveListarFilaDaCozinha() {

        ItemPedido primeiroPedido = new ItemPedido();
        primeiroPedido.setStatusPreparo(StatusPreparo.RECEBIDO);

        ItemPedido segundoPedido = new ItemPedido();
        segundoPedido.setStatusPreparo(StatusPreparo.EM_PREPARO);

        List<StatusPreparo> statusFila = List.of(
                StatusPreparo.RECEBIDO,
                StatusPreparo.EM_PREPARO
        );

        List<ItemPedido> pedidos = List.of(
                primeiroPedido,
                segundoPedido
        );

        when(itemPedidoRepository
                .findByStatusPreparoInOrderByDataHoraPedidoAsc(statusFila))
                .thenReturn(pedidos);

        List<ItemPedido> resultado = cozinhaService.listarFila();

        assertEquals(2, resultado.size());
        assertEquals(StatusPreparo.RECEBIDO,
                resultado.get(0).getStatusPreparo());
        assertEquals(StatusPreparo.EM_PREPARO,
                resultado.get(1).getStatusPreparo());

        verify(itemPedidoRepository)
                .findByStatusPreparoInOrderByDataHoraPedidoAsc(statusFila);
    }
    @Test
    void deveConverterPedidosParaFilaCozinhaResponse() {

        Produto produto = new Produto();
        produto.setNome("Hambúrguer");

        ItemPedido item = new ItemPedido();
        item.setId(1L);
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setObservacao("Sem cebola");
        item.setStatusPreparo(StatusPreparo.RECEBIDO);

        List<StatusPreparo> statusFila = List.of(
                StatusPreparo.RECEBIDO,
                StatusPreparo.EM_PREPARO
        );

        when(itemPedidoRepository
                .findByStatusPreparoInOrderByDataHoraPedidoAsc(statusFila))
                .thenReturn(List.of(item));

        List<FilaCozinhaResponse> resultado = cozinhaService.consultarFila();

        assertEquals(1, resultado.size());

        FilaCozinhaResponse pedido = resultado.get(0);

        assertEquals(1L, pedido.itemId());
        assertEquals("Hambúrguer", pedido.nomeProduto());
        assertEquals(2, pedido.quantidade());
        assertEquals("Sem cebola", pedido.observacao());
        assertEquals(StatusPreparo.RECEBIDO, pedido.statusPreparo());
        assertEquals(item.getDataHoraPedido(), pedido.dataHoraPedido());

        verify(itemPedidoRepository)
                .findByStatusPreparoInOrderByDataHoraPedidoAsc(statusFila);
    }
}

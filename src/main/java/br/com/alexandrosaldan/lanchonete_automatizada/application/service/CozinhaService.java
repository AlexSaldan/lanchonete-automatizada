package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.ItemPedido;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.ItemPedidoRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPreparo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CozinhaService {

    private final ItemPedidoRepository itemPedidoRepository;

    public CozinhaService(ItemPedidoRepository itemPedidoRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @Transactional
    public void iniciarPreparo(Long itemId) {

        ItemPedido item = itemPedidoRepository.findById(itemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Pedido não encontrado.")
                );

        if (item.getStatusPreparo() != StatusPreparo.RECEBIDO) {
            throw new IllegalStateException(
                    "Somente pedidos recebidos podem iniciar o preparo."
            );
        }

        item.setStatusPreparo(StatusPreparo.EM_PREPARO);

        itemPedidoRepository.save(item);
    }
    @Transactional
    public void marcarComoPronto(Long itemId) {

        ItemPedido item = itemPedidoRepository.findById(itemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Pedido não encontrado.")
                );

        if (item.getStatusPreparo() != StatusPreparo.EM_PREPARO) {
            throw new IllegalStateException(
                    "Somente pedidos em preparo podem ser marcados como prontos."
            );
        }

        item.setStatusPreparo(StatusPreparo.PRONTO);

        itemPedidoRepository.save(item);
    }
    @Transactional
    public void marcarComoEntregue(Long itemId) {

        ItemPedido item = itemPedidoRepository.findById(itemId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Pedido não encontrado.")
                );

        if (item.getStatusPreparo() != StatusPreparo.PRONTO) {
            throw new IllegalStateException(
                    "Somente pedidos prontos podem ser entregues."
            );
        }

        item.setStatusPreparo(StatusPreparo.ENTREGUE);

        itemPedidoRepository.save(item);
    }
}

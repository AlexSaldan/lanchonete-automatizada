package br.com.alexandrosaldan.lanchonete_automatizada.domain.repository;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.ItemPedido;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPreparo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    /**
     * Busca todos os itens pertencentes a uma subcomanda específica.
     */
    List<ItemPedido> findBySubcomandaId(Long subcomandaId);

    /**
     * Busca itens para a fila da cozinha ordenados do mais antigo para o mais recente (FIFO),
     * permitindo filtrar apenas os status relevantes para o display da cozinha.
     */
    List<ItemPedido> findByStatusPreparoInOrderByDataHoraPedidoAsc(List<StatusPreparo> statusList);
}

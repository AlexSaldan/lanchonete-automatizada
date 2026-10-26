package br.com.alexandrosaldan.lanchonete_automatizada.domain.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.ItemPedido;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPreparo;
import jakarta.persistence.LockModeType;

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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM ItemPedido i WHERE i.id = :id")
    Optional<ItemPedido> buscarPorIdComBloqueio(@Param("id") Long id);
}

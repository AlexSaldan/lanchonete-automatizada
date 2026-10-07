package br.com.alexandrosaldan.lanchonete_automatizada.domain.repository;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.SessaoMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusSessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SessaoMesaRepository extends JpaRepository<SessaoMesa, Long> {

    @Query("SELECT s FROM SessaoMesa s WHERE s.mesa.numero = :numeroMesa AND s.status = :status")
    Optional<SessaoMesa> findByMesaNumeroAndStatus(@Param("numeroMesa") Integer numeroMesa, @Param("status") StatusSessao status);

    @Query("SELECT s FROM SessaoMesa s WHERE s.mesa.tokenQrCode = :tokenQrCode AND s.status = 'ABERTA'")
    Optional<SessaoMesa> findSessaoAbertaPorTokenQrCode(@Param("tokenQrCode") String tokenQrCode);
}

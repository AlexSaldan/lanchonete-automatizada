package br.com.alexandrosaldan.lanchonete_automatizada.domain.repository;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubcomandaRepository extends JpaRepository<Subcomanda, Long> {

    List<Subcomanda> findBySessaoMesaId(Long sessaoMesaId);
    // Consulta as subcomandas com bloqueio pessimista.
    // Evita leituras antigas durante a confirmação de pagamentos simultâneos.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           SELECT s
           FROM Subcomanda s
           WHERE s.sessaoMesa.id = :sessaoMesaId
           ORDER BY s.id
           """)
    List<Subcomanda> buscarPorSessaoMesaIdComBloqueio(
            @Param("sessaoMesaId") Long sessaoMesaId
    );

    // Bloqueia a subcomanda durante operações que alteram itens ou pagamentos.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Subcomanda s WHERE s.id = :id")
    Optional<Subcomanda> buscarPorIdComBloqueio(@Param("id") Long id);

    // Descobre a sessão sem bloquear a subcomanda.
    // Permite adquirir os bloqueios na ordem: mesa -> sessão -> subcomanda.
    @Query("SELECT s.sessaoMesa.id FROM Subcomanda s WHERE s.id = :subcomandaId")
    Optional<Long> buscarSessaoIdPorSubcomandaId(
            @Param("subcomandaId") Long subcomandaId
    );
}

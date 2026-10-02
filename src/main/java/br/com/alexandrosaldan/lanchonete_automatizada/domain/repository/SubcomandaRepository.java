package br.com.alexandrosaldan.lanchonete_automatizada.domain.repository;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubcomandaRepository extends JpaRepository<Subcomanda, Long> {

    List<Subcomanda> findBySessaoMesaId(Long sessaoMesaId);
}

package br.com.alexandrosaldan.lanchonete_automatizada.domain.repository;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Produto;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.CategoriaProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    // Busca produtos de uma categoria específica que estejam disponíveis (ideal para o cardápio do totem)
    List<Produto> findByCategoriaAndDisponivelTrue(CategoriaProduto categoria);
    
    // Busca todos os produtos disponíveis
    List<Produto> findByDisponivelTrue();
}

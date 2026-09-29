package br.com.alexandrosaldan.lanchonete_automatizada.domain.repository;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Produto;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.CategoriaProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
    // Busca todos os produtos de uma categoria específica, ordenados pelo nome
    List<Produto> findByCategoriaOrderByNomeAsc(CategoriaProduto categoria);
    
    // Busca produtos pelo nome (contendo o texto, case-insensitive)
    List<Produto> findByNomeContainingIgnoreCase(String nome);
}

package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Produto;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.CategoriaProduto;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.ProdutoNaoEncontradoException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produtoX;

    @BeforeEach
    void setUp() {
        produtoX = new Produto("X-Bacon", "Hambúrguer com bacon", CategoriaProduto.LANCHE, new BigDecimal("22.90"), 12);
        produtoX.setId(1L);
    }

    @Test
    void deveBuscarProdutoPorIdQuandoExistir() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoX));

        Produto resultado = produtoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals("X-Bacon", resultado.getNome());
        verify(produtoRepository).findById(1L);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExistir() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProdutoNaoEncontradoException.class, () -> produtoService.buscarPorId(99L));
    }

    @Test
    void deveListarProdutosDisponiveis() {
        when(produtoRepository.findByDisponivelTrue()).thenReturn(Arrays.asList(produtoX));

        List<Produto> resultado = produtoService.listarDisponiveis();

        assertEquals(1, resultado.size());
        verify(produtoRepository).findByDisponivelTrue();
    }

    @Test
    void deveCriarProdutoComSucesso() {
        when(produtoRepository.save(any(Produto.class))).thenReturn(produtoX);

        Produto resultado = produtoService.criarProduto(produtoX);

        assertNotNull(resultado);
        verify(produtoRepository).save(produtoX);
    }

    @Test
    void deveDesativarProduto() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produtoX));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Produto resultado = produtoService.desativarProduto(1L);

        assertFalse(resultado.getDisponivel());
        verify(produtoRepository).save(produtoX);
    }
}

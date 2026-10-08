package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.*;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.*;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.*;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.*;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComandaServiceTest {

    @Mock
    private MesaRepository mesaRepository;

    @Mock
    private SessaoMesaRepository sessaoMesaRepository;

    @Mock
    private SubcomandaRepository subcomandaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @InjectMocks
    private ComandaService comandaService;

    private Mesa mesa;
    private SessaoMesa sessao;
    private Subcomanda subcomanda;
    private Produto produto;

    @BeforeEach
    void setUp() {

        mesa = new Mesa(1, 4);
        mesa.setId(1L);
        mesa.setStatus(StatusMesa.DISPONIVEL);
        mesa.setTokenQrCode("token-valido-123");

        sessao = new SessaoMesa(mesa);
        sessao.setId(1L);

        subcomanda = new Subcomanda(
                "João",
                "123.456.789-00"
        );
        subcomanda.setId(1L);
        subcomanda.setSessaoMesa(sessao);

        produto = new Produto(
                "X-Bacon",
                "Hambúrguer com bacon",
                CategoriaProduto.LANCHE,
                new BigDecimal("22.90"),
                12
        );
        produto.setId(1L);
    }

    @Test
    @DisplayName("Deve abrir sessão com sucesso quando mesa está disponível e token é válido")
    void deveAbrirSessaoComSucesso() {

        AberturaSessaoRequest request = new AberturaSessaoRequest(
                1,
                "token-valido-123",
                "João",
                "123.456.789-00"
        );

        when(mesaRepository.findByNumero(1))
                .thenReturn(Optional.of(mesa));

        when(sessaoMesaRepository.save(any(SessaoMesa.class)))
                .thenAnswer(invocation -> {
                    SessaoMesa sessaoSalva =
                            invocation.getArgument(0);

                    sessaoSalva.setId(1L);

                    return sessaoSalva;
                });

        when(sessaoMesaRepository.findById(1L))
                .thenReturn(Optional.of(sessao));

        ExtratoMesaResponse response =
                comandaService.abrirSessao(request);

        assertNotNull(response);

        assertEquals(
                StatusMesa.OCUPADA,
                mesa.getStatus()
        );

        assertEquals(
                1L,
                response.sessaoId()
        );

        assertEquals(
                1,
                response.numeroMesa()
        );

        verify(mesaRepository)
                .save(mesa);

        verify(sessaoMesaRepository)
                .save(any(SessaoMesa.class));

        verify(sessaoMesaRepository)
                .findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção quando token QR Code for inválido")
    void deveLancarExcecaoQuandoTokenInvalido() {

        AberturaSessaoRequest request = new AberturaSessaoRequest(
                1,
                "token-invalido",
                "João",
                null
        );

        when(mesaRepository.findByNumero(1))
                .thenReturn(Optional.of(mesa));

        IllegalArgumentException excecao =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> comandaService.abrirSessao(request)
                );

        assertEquals(
                "Token do QR Code inválido para esta mesa.",
                excecao.getMessage()
        );

        verify(mesaRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando mesa já estiver ocupada")
    void deveLancarExcecaoQuandoMesaOcupada() {

        mesa.setStatus(StatusMesa.OCUPADA);

        AberturaSessaoRequest request =
                new AberturaSessaoRequest(
                        1,
                        "token-valido-123",
                        "João",
                        null
                );

        when(mesaRepository.findByNumero(1))
                .thenReturn(Optional.of(mesa));

        IllegalStateException excecao =
                assertThrows(
                        IllegalStateException.class,
                        () -> comandaService.abrirSessao(request)
                );

        assertTrue(
                excecao.getMessage()
                        .contains("já possui uma sessão aberta")
        );
    }

    @Test
    @DisplayName("Deve adicionar cliente à sessão existente")
    void deveAdicionarClienteASessao() {

        sessao.setStatus(StatusSessao.ABERTA);

        AdicionarClienteRequest request =
                new AdicionarClienteRequest(
                        1L,
                        "Maria",
                        "987.654.321-00"
                );

        when(sessaoMesaRepository.findById(1L))
                .thenReturn(Optional.of(sessao));

        when(sessaoMesaRepository.save(any(SessaoMesa.class)))
                .thenAnswer(invocation -> {

                    SessaoMesa sessaoSalva =
                            invocation.getArgument(0);

                    sessaoSalva.getSubcomandas()
                            .stream()
                            .filter(sub -> sub.getId() == null)
                            .forEach(sub -> sub.setId(2L));

                    return sessaoSalva;
                });

        when(itemPedidoRepository.findBySubcomandaId(2L))
                .thenReturn(List.of());

        ExtratoSubcomandaResponse response =
                comandaService.adicionarCliente(request);

        assertNotNull(response);

        assertEquals(
                "Maria",
                response.nomeCliente()
        );

        assertEquals(
                StatusPagamento.PENDENTE,
                response.statusPagamento()
        );

        verify(sessaoMesaRepository)
                .save(sessao);

        verify(subcomandaRepository, never())
                .save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao adicionar cliente em sessão encerrada")
    void deveLancarExcecaoAoAdicionarClienteEmSessaoEncerrada() {

        sessao.setStatus(StatusSessao.ENCERRADA);

        AdicionarClienteRequest request =
                new AdicionarClienteRequest(
                        1L,
                        "Maria",
                        null
                );

        when(sessaoMesaRepository.findById(1L))
                .thenReturn(Optional.of(sessao));

        assertThrows(
                IllegalStateException.class,
                () -> comandaService.adicionarCliente(request)
        );
    }

    @Test
    @DisplayName("Deve lançar item na subcomanda com sucesso")
    void deveLancarItemComSucesso() {

        ItemPedidoRequest request =
                new ItemPedidoRequest(
                        1L,
                        1L,
                        2,
                        "Sem cebola"
                );

        when(subcomandaRepository.findById(1L))
                .thenReturn(Optional.of(subcomanda));

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        when(itemPedidoRepository.save(any(ItemPedido.class)))
                .thenAnswer(invocation -> {

                    ItemPedido item =
                            invocation.getArgument(0);

                    item.setId(1L);

                    return item;
                });

        ItemPedidoResponse response =
                comandaService.lancarItem(request);

        assertNotNull(response);

        assertEquals(
                "X-Bacon",
                response.nomeProduto()
        );

        assertEquals(
                2,
                response.quantidade()
        );

        assertEquals(
                new BigDecimal("45.80"),
                response.subtotal()
        );

        assertEquals(
                "Sem cebola",
                response.observacao()
        );

        assertEquals(
                StatusPreparo.RECEBIDO,
                response.statusPreparo()
        );

        verify(subcomandaRepository)
                .save(subcomanda);
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não estiver disponível")
    void deveLancarExcecaoQuandoProdutoIndisponivel() {

        produto.setDisponivel(false);

        ItemPedidoRequest request =
                new ItemPedidoRequest(
                        1L,
                        1L,
                        1,
                        null
                );

        when(subcomandaRepository.findById(1L))
                .thenReturn(Optional.of(subcomanda));

        when(produtoRepository.findById(1L))
                .thenReturn(Optional.of(produto));

        IllegalStateException excecao =
                assertThrows(
                        IllegalStateException.class,
                        () -> comandaService.lancarItem(request)
                );

        assertTrue(
                excecao.getMessage()
                        .contains("não está disponível")
        );
    }

    @Test
    @DisplayName("Deve buscar extrato da mesa com sucesso")
    void deveBuscarExtratoMesaComSucesso() {

        sessao.getSubcomandas()
                .add(subcomanda);

        when(sessaoMesaRepository.findById(1L))
                .thenReturn(Optional.of(sessao));

        when(itemPedidoRepository.findBySubcomandaId(1L))
                .thenReturn(List.of());

        ExtratoMesaResponse response =
                comandaService.buscarExtratoMesa(1L);

        assertNotNull(response);

        assertEquals(
                1,
                response.numeroMesa()
        );

        assertEquals(
                StatusSessao.ABERTA,
                response.statusSessao()
        );

        assertEquals(
                1,
                response.subcomandas().size()
        );
    }

    @Test
    @DisplayName("Deve lançar exceção quando sessão não for encontrada no extrato")
    void deveLancarExcecaoQuandoSessaoNaoEncontrada() {

        when(sessaoMesaRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                SessaoNaoEncontradaException.class,
                () -> comandaService.buscarExtratoMesa(99L)
        );
    }
}



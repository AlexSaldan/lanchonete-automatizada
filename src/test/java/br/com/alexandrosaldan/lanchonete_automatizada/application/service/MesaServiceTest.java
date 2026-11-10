package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Mesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.SessaoMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPagamento;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusSessao;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.MesaNaoEncontradaException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.MesaRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.SessaoMesaRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.SubcomandaRepository;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
class MesaServiceTest {

	@Mock
	private MesaRepository mesaRepository;

	@Mock
	private SessaoMesaRepository sessaoMesaRepository;

	@Mock
	private SubcomandaRepository subcomandaRepository;

	@InjectMocks
	private MesaService mesaService;

	private Mesa mesaDisponivel;
	private Mesa mesaOcupada;

	@BeforeEach
	void setUp() {
		mesaDisponivel = new Mesa(1, 4);
		mesaDisponivel.setId(1L);
		mesaDisponivel.setStatus(StatusMesa.DISPONIVEL);

		mesaOcupada = new Mesa(2, 2);
		mesaOcupada.setId(2L);
		mesaOcupada.setStatus(StatusMesa.OCUPADA);
	}

	@Test
	@DisplayName("Deve buscar mesa por número quando ela existir")
	void deveBuscarMesaPorNumeroQuandoExistir() {
		when(mesaRepository.findByNumero(1)).thenReturn(Optional.of(mesaDisponivel));

		Mesa resultado = mesaService.buscarPorNumero(1);

		assertNotNull(resultado);
		assertEquals(1, resultado.getNumero());
		assertEquals(StatusMesa.DISPONIVEL, resultado.getStatus());
		verify(mesaRepository, times(1)).findByNumero(1);
	}

	@Test
	@DisplayName("Deve lançar exceção quando mesa não for encontrada")
	void deveLancarExcecaoQuandoMesaNaoForEncontrada() {
		when(mesaRepository.findByNumero(99)).thenReturn(Optional.empty());

		MesaNaoEncontradaException excecao = assertThrows(MesaNaoEncontradaException.class,
				() -> mesaService.buscarPorNumero(99));

		assertEquals("Mesa com número 99 não encontrada.", excecao.getMessage());
		verify(mesaRepository, times(1)).findByNumero(99);
	}

	@Test
	@DisplayName("Deve ocupar mesa quando ela estiver disponível")
	void deveOcuparMesaQuandoEstiverDisponivel() {
		when(mesaRepository.findByNumeroComBloqueio(1))
        .thenReturn(Optional.of(mesaDisponivel));
		when(mesaRepository.save(any(Mesa.class))).thenReturn(mesaDisponivel);

		Mesa resultado = mesaService.ocuparMesa(1);

		assertEquals(StatusMesa.OCUPADA, resultado.getStatus());
		verify(mesaRepository, times(1)).save(mesaDisponivel);
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar ocupar mesa já ocupada")
	void deveLancarExcecaoAoTentarOcuparMesaOcupada() {
		when(mesaRepository.findByNumeroComBloqueio(2))
        .thenReturn(Optional.of(mesaOcupada));

		IllegalStateException excecao = assertThrows(IllegalStateException.class, () -> mesaService.ocuparMesa(2));

		assertEquals("A mesa 2 já está ocupada.", excecao.getMessage());
		verify(mesaRepository, never()).save(any());
	}

	@Test
	@DisplayName("Deve liberar mesa ocupada")
	void deveLiberarMesaOcupada() {

		SessaoMesa sessao = new SessaoMesa(mesaOcupada);
		sessao.setId(10L);

		Subcomanda joao = new Subcomanda("João", null);
		joao.setStatusPagamento(StatusPagamento.PAGO);

		Subcomanda maria = new Subcomanda("Maria", null);
		maria.setStatusPagamento(StatusPagamento.PAGO);

		sessao.adicionarSubcomanda(joao);
		sessao.adicionarSubcomanda(maria);
		when(mesaRepository.findByNumeroComBloqueio(2)).thenReturn(Optional.of(mesaOcupada));
		when(sessaoMesaRepository.findByMesaNumeroAndStatus(2, StatusSessao.ABERTA)).thenReturn(Optional.of(sessao));
		when(sessaoMesaRepository.buscarPorIdComBloqueio(10L)).thenReturn(Optional.of(sessao));
		when(subcomandaRepository.buscarPorSessaoMesaIdComBloqueio(10L)).thenReturn(sessao.getSubcomandas());
		when(mesaRepository.save(any(Mesa.class))).thenReturn(mesaOcupada);

		Mesa resultado = mesaService.liberarMesa(2);

		assertEquals(StatusMesa.DISPONIVEL, resultado.getStatus());
		assertEquals(StatusSessao.ENCERRADA, sessao.getStatus());
		verify(mesaRepository, times(1)).save(mesaOcupada);
		verify(sessaoMesaRepository, times(1)).save(sessao);
	}

	@Test
	@DisplayName("Não deve liberar mesa com subcomanda pendente")
	void naoDeveLiberarMesaComSubcomandaPendente() {

		SessaoMesa sessao = new SessaoMesa(mesaOcupada);
		sessao.setId(10L);

		Subcomanda joao = new Subcomanda("João", null);
		joao.setStatusPagamento(StatusPagamento.PAGO);

		Subcomanda maria = new Subcomanda("Maria", null);
		maria.setStatusPagamento(StatusPagamento.PENDENTE);

		sessao.adicionarSubcomanda(joao);
		sessao.adicionarSubcomanda(maria);

		when(mesaRepository.findByNumeroComBloqueio(2)).thenReturn(Optional.of(mesaOcupada));

		when(sessaoMesaRepository.findByMesaNumeroAndStatus(2, StatusSessao.ABERTA)).thenReturn(Optional.of(sessao));

		when(sessaoMesaRepository.buscarPorIdComBloqueio(10L)).thenReturn(Optional.of(sessao));

		when(subcomandaRepository.buscarPorSessaoMesaIdComBloqueio(10L)).thenReturn(sessao.getSubcomandas());

		assertThrows(IllegalStateException.class, () -> mesaService.liberarMesa(2));

		assertEquals(StatusMesa.OCUPADA, mesaOcupada.getStatus());

		verify(mesaRepository, never()).save(any(Mesa.class));
	}

	@Test
	@DisplayName("Não deve liberar mesa quando a sessão não possui subcomandas")
	void naoDeveLiberarMesaSemSubcomandas() {

	    SessaoMesa sessao = new SessaoMesa(mesaOcupada);
	    sessao.setId(10L);

	    when(mesaRepository.findByNumeroComBloqueio(2))
	            .thenReturn(Optional.of(mesaOcupada));

	    when(sessaoMesaRepository.findByMesaNumeroAndStatus(
	            2, StatusSessao.ABERTA))
	            .thenReturn(Optional.of(sessao));

	    when(sessaoMesaRepository.buscarPorIdComBloqueio(10L))
	            .thenReturn(Optional.of(sessao));

	    when(subcomandaRepository.buscarPorSessaoMesaIdComBloqueio(10L))
	            .thenReturn(java.util.Collections.emptyList());

	    assertThrows(
	            IllegalStateException.class,
	            () -> mesaService.liberarMesa(2)
	    );

	    assertEquals(StatusMesa.OCUPADA, mesaOcupada.getStatus());
	    assertEquals(StatusSessao.ABERTA, sessao.getStatus());

	    verify(mesaRepository, never()).save(any(Mesa.class));
	    verify(sessaoMesaRepository, never()).save(any(SessaoMesa.class));
	}
	@Test
	@DisplayName("Deve liberar mesa ocupada manualmente sem sessão aberta")
	void deveLiberarMesaOcupadaManualmenteSemSessao() {

	    mesaOcupada.setOcupacaoManual(true);

	    when(mesaRepository.findByNumeroComBloqueio(2))
	            .thenReturn(Optional.of(mesaOcupada));

	    when(sessaoMesaRepository.findByMesaNumeroAndStatus(
	            2, StatusSessao.ABERTA))
	            .thenReturn(Optional.empty());

	    when(mesaRepository.save(any(Mesa.class)))
	            .thenReturn(mesaOcupada);

	    Mesa resultado = mesaService.liberarMesa(2);

	    assertEquals(StatusMesa.DISPONIVEL, resultado.getStatus());
	    assertFalse(resultado.isOcupacaoManual());

	    verify(mesaRepository, times(1)).save(mesaOcupada);
	}
	 @Test
	 @DisplayName("Não deve liberar mesa sem sessão aberta e sem ocupação manual")
	 void naoDeveLiberarMesaSemSessaoAbertaESemOcupacaoManual() {

	     when(mesaRepository.findByNumeroComBloqueio(2))
	             .thenReturn(Optional.of(mesaOcupada));

	     when(sessaoMesaRepository.findByMesaNumeroAndStatus(
	             2, StatusSessao.ABERTA))
	             .thenReturn(Optional.empty());

	     assertThrows(
	             IllegalStateException.class,
	             () -> mesaService.liberarMesa(2)
	     );

	     assertEquals(StatusMesa.OCUPADA, mesaOcupada.getStatus());

	     verify(mesaRepository, never()).save(any(Mesa.class));
	 }
	 @Test
	 @DisplayName("Não deve liberar mesa que já está disponível")
	 void naoDeveLiberarMesaDisponivel() {

	     when(mesaRepository.findByNumeroComBloqueio(1))
	             .thenReturn(Optional.of(mesaDisponivel));

	     IllegalStateException excecao = assertThrows(
	             IllegalStateException.class,
	             () -> mesaService.liberarMesa(1)
	     );

	     assertEquals(
	             "A mesa 1 não está ocupada.",
	             excecao.getMessage()
	     );

	     verify(sessaoMesaRepository, never())
	             .findByMesaNumeroAndStatus(any(), any());

	     verify(mesaRepository, never()).save(any(Mesa.class));
	 }
}

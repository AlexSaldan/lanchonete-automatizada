package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPagamento;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.SubcomandaNaoEncontradaException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.SubcomandaRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.MesaRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.SessaoMesaRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Mesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.SessaoMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusSessao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagamentoServiceTest {

	@Mock
	private SubcomandaRepository subcomandaRepository;

	@InjectMocks
	private PagamentoService pagamentoService;

	private Subcomanda subcomanda;

	@BeforeEach
	void configurar() {
		subcomanda = new Subcomanda("João", null);
		subcomanda.setId(1L);
		subcomanda.setValorTotal(new BigDecimal("45.80"));
		subcomanda.setStatusPagamento(StatusPagamento.PENDENTE);
	}

	@Test
	void deveIniciarPagamentoPendente() {

		// Preparação da mesa
		Mesa mesa = new Mesa(1, 4);
		mesa.setId(10L);
		mesa.setStatus(StatusMesa.OCUPADA);

		// Preparação da sessão
		SessaoMesa sessao = new SessaoMesa(mesa);
		sessao.setId(20L);

		// Vincula João à sessão
		sessao.adicionarSubcomanda(subcomanda);

		// Consulta dos relacionamentos
		when(subcomandaRepository.buscarSessaoIdPorSubcomandaId(1L)).thenReturn(Optional.of(20L));

		when(sessaoMesaRepository.buscarMesaIdPorSessaoId(20L)).thenReturn(Optional.of(10L));

		// Bloqueios pessimistas
		when(mesaRepository.findByIdComBloqueio(10L)).thenReturn(Optional.of(mesa));

		when(sessaoMesaRepository.buscarPorIdComBloqueio(20L)).thenReturn(Optional.of(sessao));

		when(subcomandaRepository.buscarPorIdComBloqueio(1L)).thenReturn(Optional.of(subcomanda));

		// Persistência simulada
		when(subcomandaRepository.save(any(Subcomanda.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Executa
		Subcomanda resultado = pagamentoService.iniciarPagamento(1L);

		// Verifica o pagamento
		assertEquals(StatusPagamento.PROCESSANDO, resultado.getStatusPagamento());

		// Verifica a persistência
		verify(subcomandaRepository).save(subcomanda);

		// Verifica a ordem dos bloqueios
		var ordem = inOrder(mesaRepository, sessaoMesaRepository, subcomandaRepository);

		ordem.verify(mesaRepository).findByIdComBloqueio(10L);
		ordem.verify(sessaoMesaRepository).buscarPorIdComBloqueio(20L);
		ordem.verify(subcomandaRepository).buscarPorIdComBloqueio(1L);
	}

	@Test
	void naoDeveIniciarPagamentoJaPago() {

		// Mesa ocupada
		Mesa mesa = new Mesa(1, 4);
		mesa.setId(10L);
		mesa.setStatus(StatusMesa.OCUPADA);

		// Sessão aberta
		SessaoMesa sessao = new SessaoMesa(mesa);
		sessao.setId(20L);

		// João já pagou
		subcomanda.setStatusPagamento(StatusPagamento.PAGO);
		sessao.adicionarSubcomanda(subcomanda);

		// Consultas dos relacionamentos
		when(subcomandaRepository.buscarSessaoIdPorSubcomandaId(1L)).thenReturn(Optional.of(20L));

		when(sessaoMesaRepository.buscarMesaIdPorSessaoId(20L)).thenReturn(Optional.of(10L));

		// Bloqueios
		when(mesaRepository.findByIdComBloqueio(10L)).thenReturn(Optional.of(mesa));

		when(sessaoMesaRepository.buscarPorIdComBloqueio(20L)).thenReturn(Optional.of(sessao));

		when(subcomandaRepository.buscarPorIdComBloqueio(1L)).thenReturn(Optional.of(subcomanda));

		// Deve impedir um novo pagamento
		assertThrows(IllegalStateException.class, () -> pagamentoService.iniciarPagamento(1L));

		// Não pode salvar alterações
		verify(subcomandaRepository, never()).save(any());
	}

	@Test
	void naoDeveIniciarPagamentoComValorZero() {

		// Mesa ocupada
		Mesa mesa = new Mesa(1, 4);
		mesa.setId(10L);
		mesa.setStatus(StatusMesa.OCUPADA);

		// Sessão aberta
		SessaoMesa sessao = new SessaoMesa(mesa);
		sessao.setId(20L);

		// João possui uma subcomanda com valor zero
		subcomanda.setValorTotal(BigDecimal.ZERO);
		sessao.adicionarSubcomanda(subcomanda);

		// Consultas dos relacionamentos
		when(subcomandaRepository.buscarSessaoIdPorSubcomandaId(1L)).thenReturn(Optional.of(20L));

		when(sessaoMesaRepository.buscarMesaIdPorSessaoId(20L)).thenReturn(Optional.of(10L));

		// Bloqueios pessimistas
		when(mesaRepository.findByIdComBloqueio(10L)).thenReturn(Optional.of(mesa));

		when(sessaoMesaRepository.buscarPorIdComBloqueio(20L)).thenReturn(Optional.of(sessao));

		when(subcomandaRepository.buscarPorIdComBloqueio(1L)).thenReturn(Optional.of(subcomanda));

		// Pagamento com valor zero deve ser rejeitado
		assertThrows(IllegalStateException.class, () -> pagamentoService.iniciarPagamento(1L));

		// Nenhuma alteração deve ser salva
		verify(subcomandaRepository, never()).save(any());

		// O pagamento deve continuar pendente
		assertEquals(StatusPagamento.PENDENTE, subcomanda.getStatusPagamento());
	}

	@Mock
	private MesaRepository mesaRepository;

	@Mock
	private SessaoMesaRepository sessaoMesaRepository;

	@Test
	void naoDeveIniciarPagamentoComValorNegativo() {

		// Mesa ocupada
		Mesa mesa = new Mesa(1, 4);
		mesa.setId(10L);
		mesa.setStatus(StatusMesa.OCUPADA);

		// Sessão aberta
		SessaoMesa sessao = new SessaoMesa(mesa);
		sessao.setId(20L);

		// João tenta pagar um valor negativo
		subcomanda.setValorTotal(new BigDecimal("-25.00"));
		sessao.adicionarSubcomanda(subcomanda);

		// Consultas dos relacionamentos
		when(subcomandaRepository.buscarSessaoIdPorSubcomandaId(1L)).thenReturn(Optional.of(20L));

		when(sessaoMesaRepository.buscarMesaIdPorSessaoId(20L)).thenReturn(Optional.of(10L));

		// Bloqueios pessimistas
		when(mesaRepository.findByIdComBloqueio(10L)).thenReturn(Optional.of(mesa));

		when(sessaoMesaRepository.buscarPorIdComBloqueio(20L)).thenReturn(Optional.of(sessao));

		when(subcomandaRepository.buscarPorIdComBloqueio(1L)).thenReturn(Optional.of(subcomanda));

		// O pagamento negativo deve ser rejeitado
		assertThrows(IllegalStateException.class, () -> pagamentoService.iniciarPagamento(1L));

		// Nenhuma alteração deve ser salva
		verify(subcomandaRepository, never()).save(any());

		// A subcomanda deve continuar pendente
		assertEquals(StatusPagamento.PENDENTE, subcomanda.getStatusPagamento());
	}

	@Test
	void deveConfirmarPagamentoEmProcessamento() {

		Mesa mesa = new Mesa(1, 4);
		mesa.setId(10L);
		mesa.setStatus(StatusMesa.OCUPADA);

		SessaoMesa sessao = new SessaoMesa(mesa);
		sessao.setId(20L);

		// João está confirmando o pagamento
		subcomanda.setStatusPagamento(StatusPagamento.PROCESSANDO);
		sessao.adicionarSubcomanda(subcomanda);

		// Maria ainda não pagou
		Subcomanda maria = new Subcomanda("Maria", null);
		maria.setId(2L);
		maria.setStatusPagamento(StatusPagamento.PENDENTE);
		maria.setValorTotal(new BigDecimal("30.00"));
		sessao.adicionarSubcomanda(maria);

		when(subcomandaRepository.buscarSessaoIdPorSubcomandaId(1L)).thenReturn(Optional.of(20L));

		when(sessaoMesaRepository.buscarMesaIdPorSessaoId(20L)).thenReturn(Optional.of(10L));

		when(mesaRepository.findByIdComBloqueio(10L)).thenReturn(Optional.of(mesa));

		when(sessaoMesaRepository.buscarPorIdComBloqueio(20L)).thenReturn(Optional.of(sessao));

		when(subcomandaRepository.buscarPorIdComBloqueio(1L)).thenReturn(Optional.of(subcomanda));

		when(subcomandaRepository.buscarPorSessaoMesaIdComBloqueio(20L)).thenReturn(List.of(subcomanda, maria));

		when(subcomandaRepository.save(any(Subcomanda.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Subcomanda resultado = pagamentoService.confirmarPagamento(1L);

		assertEquals(StatusPagamento.PAGO, resultado.getStatusPagamento());

		// Maria ainda deve: a sessão permanece aberta
		assertEquals(StatusSessao.ABERTA, sessao.getStatus());
		assertEquals(StatusMesa.OCUPADA, mesa.getStatus());

		verify(subcomandaRepository).save(subcomanda);
	}

	@Test
	void naoDeveConfirmarPagamentoPendente() {

		Mesa mesa = new Mesa(1, 4);
		mesa.setId(10L);
		mesa.setStatus(StatusMesa.OCUPADA);
		mesa.setOcupacaoManual(true);

		SessaoMesa sessao = new SessaoMesa(mesa);
		sessao.setId(20L);

		// João ainda não iniciou o pagamento
		subcomanda.setStatusPagamento(StatusPagamento.PENDENTE);
		sessao.adicionarSubcomanda(subcomanda);

		// Consultas dos relacionamentos
		when(subcomandaRepository.buscarSessaoIdPorSubcomandaId(1L)).thenReturn(Optional.of(20L));

		when(sessaoMesaRepository.buscarMesaIdPorSessaoId(20L)).thenReturn(Optional.of(10L));

		// Bloqueios pessimistas
		when(mesaRepository.findByIdComBloqueio(10L)).thenReturn(Optional.of(mesa));

		when(sessaoMesaRepository.buscarPorIdComBloqueio(20L)).thenReturn(Optional.of(sessao));

		when(subcomandaRepository.buscarPorIdComBloqueio(1L)).thenReturn(Optional.of(subcomanda));

		// Não pode confirmar pagamento que ainda está PENDENTE
		assertThrows(IllegalStateException.class, () -> pagamentoService.confirmarPagamento(1L));

		// Nenhuma alteração deve ser salva
		verify(subcomandaRepository, never()).save(any());

		// O estado das entidades deve permanecer inalterado
		assertEquals(StatusPagamento.PENDENTE, subcomanda.getStatusPagamento());
		assertEquals(StatusSessao.ABERTA, sessao.getStatus());
		assertEquals(StatusMesa.OCUPADA, mesa.getStatus());
	}

	@Test
	void deveLancarExcecaoQuandoSubcomandaNaoExiste() {

		when(subcomandaRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(SubcomandaNaoEncontradaException.class, () -> pagamentoService.consultarSubcomanda(99L));

		verify(subcomandaRepository, never()).save(any());
	}

	@Test
	void naoDeveIniciarPagamentoComValorNulo() {

		// Mesa ocupada
		Mesa mesa = new Mesa(1, 4);
		mesa.setId(10L);
		mesa.setStatus(StatusMesa.OCUPADA);

		// Sessão aberta
		SessaoMesa sessao = new SessaoMesa(mesa);
		sessao.setId(20L);

		// João possui uma subcomanda sem valor definido
		subcomanda.setValorTotal(null);
		sessao.adicionarSubcomanda(subcomanda);

		// Consultas dos relacionamentos
		when(subcomandaRepository.buscarSessaoIdPorSubcomandaId(1L)).thenReturn(Optional.of(20L));

		when(sessaoMesaRepository.buscarMesaIdPorSessaoId(20L)).thenReturn(Optional.of(10L));

		// Bloqueios pessimistas
		when(mesaRepository.findByIdComBloqueio(10L)).thenReturn(Optional.of(mesa));

		when(sessaoMesaRepository.buscarPorIdComBloqueio(20L)).thenReturn(Optional.of(sessao));

		when(subcomandaRepository.buscarPorIdComBloqueio(1L)).thenReturn(Optional.of(subcomanda));

		// Deve rejeitar pagamento sem valor definido
		assertThrows(IllegalStateException.class, () -> pagamentoService.iniciarPagamento(1L));

		// Nenhuma alteração deve ser salva
		verify(subcomandaRepository, never()).save(any());

		// O pagamento deve continuar pendente
		assertEquals(StatusPagamento.PENDENTE, subcomanda.getStatusPagamento());
	}

	@Test
	void naoDeveIniciarPagamentoQuandoSubcomandaNaoExiste() {

		// A subcomanda 99 não possui sessão
		when(subcomandaRepository.buscarSessaoIdPorSubcomandaId(99L)).thenReturn(Optional.empty());

		// Deve rejeitar a tentativa de pagamento
		assertThrows(SubcomandaNaoEncontradaException.class, () -> pagamentoService.iniciarPagamento(99L));

		// Não deve tentar bloquear uma subcomanda inexistente
		verify(subcomandaRepository, never()).buscarPorIdComBloqueio(anyLong());

		// Nenhuma alteração deve ser salva
		verify(subcomandaRepository, never()).save(any());
	}

	@Test
	void naoDeveConfirmarPagamentoQuandoSubcomandaNaoExiste() {

		// A subcomanda inexistente não possui sessão
		when(subcomandaRepository.buscarSessaoIdPorSubcomandaId(99L)).thenReturn(Optional.empty());

		// Deve rejeitar a confirmação
		assertThrows(SubcomandaNaoEncontradaException.class, () -> pagamentoService.confirmarPagamento(99L));

		// Não deve tentar bloquear uma subcomanda inexistente
		verify(subcomandaRepository, never()).buscarPorIdComBloqueio(anyLong());

		// Nenhuma alteração deve ser salva
		verify(subcomandaRepository, never()).save(any());
	}

	@Test
	void naoDeveLiberarMesaQuandoExistirSubcomandaPendente() {

		Mesa mesa = new Mesa(1, 4);
		mesa.setId(10L);
		mesa.setStatus(StatusMesa.OCUPADA);

		SessaoMesa sessao = new SessaoMesa(mesa);
		sessao.setId(20L);

		subcomanda.setStatusPagamento(StatusPagamento.PROCESSANDO);
		sessao.adicionarSubcomanda(subcomanda);

		Subcomanda maria = new Subcomanda("Maria", null);
		maria.setId(2L);
		maria.setValorTotal(new BigDecimal("30.00"));
		sessao.adicionarSubcomanda(maria);

		// Localiza a sessão da subcomanda
		when(subcomandaRepository.buscarSessaoIdPorSubcomandaId(1L)).thenReturn(Optional.of(20L));

		// Localiza a mesa da sessão
		when(sessaoMesaRepository.buscarMesaIdPorSessaoId(20L)).thenReturn(Optional.of(10L));

		// Bloqueia a mesa
		when(mesaRepository.findByIdComBloqueio(10L)).thenReturn(Optional.of(mesa));

		// Bloqueia a sessão
		when(sessaoMesaRepository.buscarPorIdComBloqueio(20L)).thenReturn(Optional.of(sessao));

		// Bloqueia a subcomanda
		when(subcomandaRepository.buscarPorIdComBloqueio(1L)).thenReturn(Optional.of(subcomanda));

		// Consulta todas as subcomandas da sessão
		when(subcomandaRepository.buscarPorSessaoMesaIdComBloqueio(20L)).thenReturn(List.of(subcomanda, maria));

		when(subcomandaRepository.save(any(Subcomanda.class))).thenAnswer(invocation -> invocation.getArgument(0));

		pagamentoService.confirmarPagamento(1L);

		assertEquals(StatusPagamento.PAGO, subcomanda.getStatusPagamento());
		assertEquals(StatusPagamento.PENDENTE, maria.getStatusPagamento());

		assertEquals(StatusSessao.ABERTA, sessao.getStatus());
		assertEquals(StatusMesa.OCUPADA, mesa.getStatus());
	}

	@Test
	void deveEncerrarSessaoELiberarMesaQuandoUltimaSubcomandaForPaga() {

		Mesa mesa = new Mesa(1, 4);
		mesa.setId(10L);
		mesa.setStatus(StatusMesa.OCUPADA);

		SessaoMesa sessao = new SessaoMesa(mesa);
		sessao.setId(20L);

		subcomanda.setStatusPagamento(StatusPagamento.PROCESSANDO);
		sessao.adicionarSubcomanda(subcomanda);

		Subcomanda maria = new Subcomanda("Maria", null);
		maria.setId(2L);
		maria.setStatusPagamento(StatusPagamento.PAGO);
		sessao.adicionarSubcomanda(maria);

		// Localiza a sessão da subcomanda
		when(subcomandaRepository.buscarSessaoIdPorSubcomandaId(1L)).thenReturn(Optional.of(20L));

		// Localiza a mesa da sessão
		when(sessaoMesaRepository.buscarMesaIdPorSessaoId(20L)).thenReturn(Optional.of(10L));

		// Bloqueia a mesa
		when(mesaRepository.findByIdComBloqueio(10L)).thenReturn(Optional.of(mesa));

		// Bloqueia a sessão
		when(sessaoMesaRepository.buscarPorIdComBloqueio(20L)).thenReturn(Optional.of(sessao));

		// Bloqueia a subcomanda
		when(subcomandaRepository.buscarPorIdComBloqueio(1L)).thenReturn(Optional.of(subcomanda));

		// Retorna todas as subcomandas da sessão
		when(subcomandaRepository.buscarPorSessaoMesaIdComBloqueio(20L)).thenReturn(List.of(subcomanda, maria));

		when(subcomandaRepository.save(any(Subcomanda.class))).thenAnswer(invocation -> invocation.getArgument(0));

		pagamentoService.confirmarPagamento(1L);

		assertEquals(StatusPagamento.PAGO, subcomanda.getStatusPagamento());
		assertEquals(StatusSessao.ENCERRADA, sessao.getStatus());
		assertEquals(StatusMesa.DISPONIVEL, mesa.getStatus());
		assertNotNull(sessao.getDataEncerramento());
	}   
	@Test
	void deveEncerrarSessaoQuandoOutraSubcomandaNaoTemConsumo() {

		Mesa mesa = new Mesa(1, 4);
		mesa.setId(10L);
		mesa.setStatus(StatusMesa.OCUPADA);
		mesa.setOcupacaoManual(true);

	    SessaoMesa sessao = new SessaoMesa(mesa);
	    sessao.setId(20L);

	    // João consumiu e está confirmando o pagamento
	    subcomanda.setStatusPagamento(StatusPagamento.PROCESSANDO);
	    subcomanda.setValorTotal(new BigDecimal("50.00"));
	    sessao.adicionarSubcomanda(subcomanda);

	    // Maria não consumiu nada
	    Subcomanda maria = new Subcomanda("Maria", null);
	    maria.setId(2L);
	    maria.setStatusPagamento(StatusPagamento.PENDENTE);
	    maria.setValorTotal(BigDecimal.ZERO);
	    sessao.adicionarSubcomanda(maria);

	    when(subcomandaRepository.buscarSessaoIdPorSubcomandaId(1L))
	            .thenReturn(Optional.of(20L));

	    when(sessaoMesaRepository.buscarMesaIdPorSessaoId(20L))
	            .thenReturn(Optional.of(10L));

	    when(mesaRepository.findByIdComBloqueio(10L))
	            .thenReturn(Optional.of(mesa));

	    when(sessaoMesaRepository.buscarPorIdComBloqueio(20L))
	            .thenReturn(Optional.of(sessao));

	    when(subcomandaRepository.buscarPorIdComBloqueio(1L))
	            .thenReturn(Optional.of(subcomanda));

	    when(subcomandaRepository.buscarPorSessaoMesaIdComBloqueio(20L))
	            .thenReturn(List.of(subcomanda, maria));

	    when(subcomandaRepository.save(any(Subcomanda.class)))
	            .thenAnswer(invocation -> invocation.getArgument(0));

	    // Confirma o pagamento de João
	    pagamentoService.confirmarPagamento(1L);

	    // João pagou e Maria não possui dívida
	    assertEquals(StatusPagamento.PAGO, subcomanda.getStatusPagamento());
	    assertEquals(StatusPagamento.PENDENTE, maria.getStatusPagamento());

	    // A sessão deve ser encerrada automaticamente
	    assertEquals(StatusSessao.ENCERRADA, sessao.getStatus());
	    assertEquals(StatusMesa.DISPONIVEL, mesa.getStatus());
	    assertNotNull(sessao.getDataEncerramento());
	    assertFalse(mesa.isOcupacaoManual());
	}
}

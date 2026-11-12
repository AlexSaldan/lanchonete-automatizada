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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PagamentoService {

	private static final Logger log = LoggerFactory.getLogger(PagamentoService.class);

	private final SubcomandaRepository subcomandaRepository;
	private final SessaoMesaRepository sessaoMesaRepository;
	private final MesaRepository mesaRepository;

	public PagamentoService(SubcomandaRepository subcomandaRepository, SessaoMesaRepository sessaoMesaRepository,
			MesaRepository mesaRepository) {
		this.subcomandaRepository = subcomandaRepository;
		this.sessaoMesaRepository = sessaoMesaRepository;
		this.mesaRepository = mesaRepository;
	}

	/**
	 * Consulta uma subcomanda para pagamento.
	 */
	@Transactional(readOnly = true)
	public Subcomanda consultarSubcomanda(Long subcomandaId) {
		return buscarSubcomanda(subcomandaId);
	}

	/**
	 * Inicia o processamento do pagamento.
	 *
	 * Transição permitida: PENDENTE -> PROCESSANDO
	 */
	@Transactional
	public Subcomanda iniciarPagamento(Long subcomandaId) {

		// Bloqueia mesa, sessão e subcomanda nesta ordem.
		Subcomanda subcomanda = buscarSubcomandaComBloqueios(subcomandaId);

		if (subcomanda.getStatusPagamento() != StatusPagamento.PENDENTE) {
			throw new IllegalStateException("Somente subcomandas pendentes podem iniciar pagamento.");
		}

		if (subcomanda.getValorTotal() == null || subcomanda.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalStateException("Não é possível pagar uma subcomanda sem valor.");
		}

		subcomanda.setStatusPagamento(StatusPagamento.PROCESSANDO);

		log.info("Pagamento iniciado para a subcomanda ID {}", subcomandaId);

		return subcomandaRepository.save(subcomanda);
	}

	/**
	 * Confirma administrativamente um pagamento simulado.
	 *
	 * Transição permitida: PROCESSANDO -> PAGO
	 *
	 * Não representa confirmação bancária.
	 */
	@Transactional
	public Subcomanda confirmarPagamento(Long subcomandaId) {

		Subcomanda subcomanda = buscarSubcomandaComBloqueios(subcomandaId);

		if (subcomanda.getStatusPagamento() != StatusPagamento.PROCESSANDO) {
			throw new IllegalStateException("Somente pagamentos em processamento podem ser confirmados.");
		}

		subcomanda.setStatusPagamento(StatusPagamento.PAGO);

		SessaoMesa sessao = subcomanda.getSessaoMesa();

		List<Subcomanda> subcomandas = subcomandaRepository.buscarPorSessaoMesaIdComBloqueio(sessao.getId());

		boolean todasRegularizadas = !subcomandas.isEmpty()
		        && subcomandas.stream()
		                .allMatch(Subcomanda::estaRegularizadaParaEncerramento);

		if (todasRegularizadas) {
		    sessao.encerrarSessao();
		    sessao.getMesa().setOcupacaoManual(false);
		    sessao.getMesa().setStatus(StatusMesa.DISPONIVEL);
		}

		log.info("Pagamento simulado confirmado para a subcomanda ID {}", subcomandaId);

		return subcomandaRepository.save(subcomanda);
	}

	private Subcomanda buscarSubcomandaComBloqueios(Long subcomandaId) {

		Long sessaoId = subcomandaRepository.buscarSessaoIdPorSubcomandaId(subcomandaId).orElseThrow(
				() -> new SubcomandaNaoEncontradaException("Subcomanda não encontrada com ID: " + subcomandaId));

		Long mesaId = sessaoMesaRepository.buscarMesaIdPorSessaoId(sessaoId)
				.orElseThrow(() -> new IllegalStateException("Mesa não encontrada para a sessão: " + sessaoId));

		Mesa mesa = mesaRepository.findByIdComBloqueio(mesaId)
				.orElseThrow(() -> new IllegalStateException("Mesa não encontrada com ID: " + mesaId));

		SessaoMesa sessao = sessaoMesaRepository.buscarPorIdComBloqueio(sessaoId)
				.orElseThrow(() -> new IllegalStateException("Sessão não encontrada com ID: " + sessaoId));

		Subcomanda subcomanda = subcomandaRepository.buscarPorIdComBloqueio(subcomandaId).orElseThrow(
				() -> new SubcomandaNaoEncontradaException("Subcomanda não encontrada com ID: " + subcomandaId));

		if (!sessao.getMesa().getId().equals(mesa.getId()) || !subcomanda.getSessaoMesa().getId().equals(sessaoId)) {
			throw new IllegalStateException("Os vínculos entre mesa, sessão e subcomanda são inconsistentes.");
		}

		if (sessao.getStatus() != StatusSessao.ABERTA) {
			throw new IllegalStateException("Não é possível processar pagamentos de uma sessão encerrada.");
		}

		if (mesa.getStatus() != StatusMesa.OCUPADA) {
			throw new IllegalStateException("Não é possível processar pagamentos de uma mesa desocupada.");
		}

		return subcomanda;
	}

	private Subcomanda buscarSubcomanda(Long subcomandaId) {

		return subcomandaRepository.findById(subcomandaId).orElseThrow(
				() -> new SubcomandaNaoEncontradaException("Subcomanda não encontrada com ID: " + subcomandaId));
	}
}

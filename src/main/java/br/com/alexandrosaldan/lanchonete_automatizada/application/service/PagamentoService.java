package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPagamento;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.SubcomandaNaoEncontradaException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.SubcomandaRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PagamentoService {

    private static final Logger log =
            LoggerFactory.getLogger(PagamentoService.class);

    private final SubcomandaRepository subcomandaRepository;

    public PagamentoService(SubcomandaRepository subcomandaRepository) {
        this.subcomandaRepository = subcomandaRepository;
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
     * Transição permitida:
     * PENDENTE -> PROCESSANDO
     */
    @Transactional
    public Subcomanda iniciarPagamento(Long subcomandaId) {

        Subcomanda subcomanda = buscarSubcomanda(subcomandaId);

        if (subcomanda.getStatusPagamento() != StatusPagamento.PENDENTE) {
            throw new IllegalStateException(
                    "Somente subcomandas pendentes podem iniciar pagamento."
            );
        }

        if (subcomanda.getValorTotal() == null ||
                subcomanda.getValorTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException(
                    "Não é possível pagar uma subcomanda sem valor."
            );
        }

        subcomanda.setStatusPagamento(StatusPagamento.PROCESSANDO);

        log.info(
                "Pagamento iniciado para a subcomanda ID {}",
                subcomandaId
        );

        return subcomandaRepository.save(subcomanda);
    }

    /**
     * Confirma administrativamente um pagamento simulado.
     *
     * Transição permitida:
     * PROCESSANDO -> PAGO
     *
     * Não representa confirmação bancária.
     */
    @Transactional
    public Subcomanda confirmarPagamento(Long subcomandaId) {

        Subcomanda subcomanda = buscarSubcomanda(subcomandaId);

        if (subcomanda.getStatusPagamento() != StatusPagamento.PROCESSANDO) {
            throw new IllegalStateException(
                    "Somente pagamentos em processamento podem ser confirmados."
            );
        }

        subcomanda.setStatusPagamento(StatusPagamento.PAGO);

        log.info(
                "Pagamento simulado confirmado para a subcomanda ID {}",
                subcomandaId
        );

        return subcomandaRepository.save(subcomanda);
    }

    private Subcomanda buscarSubcomanda(Long subcomandaId) {

        return subcomandaRepository.findById(subcomandaId)
                .orElseThrow(() ->
                        new SubcomandaNaoEncontradaException(
                                "Subcomanda não encontrada com ID: " + subcomandaId
                        )
                );
    }
}

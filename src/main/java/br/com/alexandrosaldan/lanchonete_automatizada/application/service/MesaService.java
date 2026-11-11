package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Mesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.MesaNaoEncontradaException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.MesaRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.SessaoMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusSessao;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.SessaoMesaRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.SubcomandaRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPagamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável por orquestrar as regras de negócio das Mesas.
 * Garante a integridade dos estados (DISPONIVEL, OCUPADA, RESERVADA)
 * antes de qualquer persistência no banco de dados.
 */
@Service
public class MesaService {

    private final MesaRepository mesaRepository;

    private final SessaoMesaRepository sessaoMesaRepository;
    private final SubcomandaRepository subcomandaRepository;

    // Injeção via construtor: prática recomendada para imutabilidade e facilidade em testes unitários
    public MesaService(
            MesaRepository mesaRepository,
            SessaoMesaRepository sessaoMesaRepository,
            SubcomandaRepository subcomandaRepository) {

        this.mesaRepository = mesaRepository;
        this.sessaoMesaRepository = sessaoMesaRepository;
        this.subcomandaRepository = subcomandaRepository;
    }

    /**
     * Busca uma mesa pelo seu número identificador.
     *
     * @param numero o número da mesa a ser buscada
     * @return a entidade Mesa encontrada
     * @throws MesaNaoEncontradaException se nenhuma mesa com o número informado existir
     */
    @Transactional(readOnly = true)
    public Mesa buscarPorNumero(Integer numero) {
        return mesaRepository.findByNumero(numero)
                .orElseThrow(() -> new MesaNaoEncontradaException("Mesa com número " + numero + " não encontrada."));
    }

    /**
     * Altera o status da mesa para OCUPADA.
     * Valida se a mesa está disponível para evitar conflitos de comanda.
     *
     * @param numero o número da mesa a ser ocupada
     * @return a entidade Mesa com o status atualizado
     * @throws IllegalStateException se a mesa já estiver ocupada
     */
    @Transactional
    public Mesa ocuparMesa(Integer numero) {
        Mesa mesa = mesaRepository.findByNumeroComBloqueio(numero)
                .orElseThrow(() -> new MesaNaoEncontradaException(
                        "Mesa com número " + numero + " não encontrada."
                ));

        if (mesa.getStatus() == StatusMesa.OCUPADA) {
            throw new IllegalStateException("A mesa " + numero + " já está ocupada.");
        }

        mesa.setStatus(StatusMesa.OCUPADA);
        mesa.setOcupacaoManual(true);

        return mesaRepository.save(mesa);
    }

    /**
     * Libera a mesa, retornando seu status para DISPONIVEL.
     *
     * @param numero o número da mesa a ser liberada
     * @return a entidade Mesa com o status atualizado
     */
    @Transactional
    public Mesa liberarMesa(Integer numero) {
        Mesa mesa = mesaRepository.findByNumeroComBloqueio(numero)
                .orElseThrow(() -> new MesaNaoEncontradaException(
                        "Mesa com número " + numero + " não encontrada."
                ));
        if (mesa.getStatus() != StatusMesa.OCUPADA) {
            throw new IllegalStateException(
                    "A mesa " + numero + " não está ocupada."
            );
        }
        Optional<SessaoMesa> sessaoEncontrada =
                sessaoMesaRepository.findByMesaNumeroAndStatus(
                        numero, StatusSessao.ABERTA
                );

        if (sessaoEncontrada.isEmpty()) {

            if (!mesa.isOcupacaoManual()) {
                throw new IllegalStateException(
                        "A mesa " + numero
                                + " não possui ocupação manual ativa nem sessão aberta."
                );
            }

            mesa.setOcupacaoManual(false);
            mesa.setStatus(StatusMesa.DISPONIVEL);

            return mesaRepository.save(mesa);
        }

        SessaoMesa sessao = sessaoEncontrada.get();
        sessao = sessaoMesaRepository.buscarPorIdComBloqueio(sessao.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Sessão não encontrada durante a liberação da mesa " + numero + "."
                ));
        if (sessao.getStatus() != StatusSessao.ABERTA) {
            throw new IllegalStateException(
                    "A sessão da mesa " + numero + " não está aberta."
            );
        }
        if (!sessao.getMesa().getId().equals(mesa.getId())) {
            throw new IllegalStateException(
                    "A sessão não pertence à mesa " + numero + "."
            );
        }
        List<Subcomanda> subcomandas =
                subcomandaRepository.buscarPorSessaoMesaIdComBloqueio(sessao.getId());

        boolean existePagamentoPendente = subcomandas.isEmpty()
                || subcomandas.stream()
                        .anyMatch(subcomanda ->
                                !subcomanda.estaRegularizadaParaEncerramento()
                        );

        if (existePagamentoPendente) {
            throw new IllegalStateException(
                    "Não é possível liberar a mesa " + numero
                            + ": existem subcomandas com pagamento pendente."
            );
        }

        sessao.encerrarSessao();
        sessaoMesaRepository.save(sessao);

        mesa.setOcupacaoManual(false);
        mesa.setStatus(StatusMesa.DISPONIVEL);

        return mesaRepository.save(mesa);
    }
}

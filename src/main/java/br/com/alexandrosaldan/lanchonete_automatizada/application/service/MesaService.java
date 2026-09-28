package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Mesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.MesaNaoEncontradaException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.MesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço responsável por orquestrar as regras de negócio das Mesas.
 * Garante a integridade dos estados (DISPONIVEL, OCUPADA, RESERVADA) 
 * antes de qualquer persistência no banco de dados.
 */
@Service
public class MesaService {

    private final MesaRepository mesaRepository;

    // Injeção via construtor: prática recomendada para imutabilidade e facilidade em testes unitários
    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
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
        Mesa mesa = buscarPorNumero(numero);
        
        if (mesa.getStatus() == StatusMesa.OCUPADA) {
            throw new IllegalStateException("A mesa " + numero + " já está ocupada.");
        }
        
        mesa.setStatus(StatusMesa.OCUPADA);
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
        Mesa mesa = buscarPorNumero(numero);
        mesa.setStatus(StatusMesa.DISPONIVEL);
        return mesaRepository.save(mesa);
    }
}

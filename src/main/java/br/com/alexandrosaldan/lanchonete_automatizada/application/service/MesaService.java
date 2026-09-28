package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Mesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.MesaNaoEncontradaException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.MesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;

    // Injeção de dependência via construtor (prática recomendada, mais segura que @Autowired no campo)
    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    @Transactional(readOnly = true)
    public Mesa buscarPorNumero(Integer numero) {
        return mesaRepository.findByNumero(numero)
                .orElseThrow(() -> new MesaNaoEncontradaException("Mesa com número " + numero + " não encontrada."));
    }

    @Transactional
    public Mesa ocuparMesa(Integer numero) {
        Mesa mesa = buscarPorNumero(numero);
        
        if (mesa.getStatus() == StatusMesa.OCUPADA) {
            throw new IllegalStateException("A mesa " + numero + " já está ocupada.");
        }
        
        mesa.setStatus(StatusMesa.OCUPADA);
        return mesaRepository.save(mesa);
    }

    @Transactional
    public Mesa liberarMesa(Integer numero) {
        Mesa mesa = buscarPorNumero(numero);
        mesa.setStatus(StatusMesa.DISPONIVEL);
        return mesaRepository.save(mesa);
    }
}

package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Mesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.exception.MesaNaoEncontradaException;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.MesaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MesaServiceTest {

    @Mock
    private MesaRepository mesaRepository;

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

        MesaNaoEncontradaException excecao = assertThrows(
                MesaNaoEncontradaException.class,
                () -> mesaService.buscarPorNumero(99)
        );

        assertEquals("Mesa com número 99 não encontrada.", excecao.getMessage());
        verify(mesaRepository, times(1)).findByNumero(99);
    }

    @Test
    @DisplayName("Deve ocupar mesa quando ela estiver disponível")
    void deveOcuparMesaQuandoEstiverDisponivel() {
        when(mesaRepository.findByNumero(1)).thenReturn(Optional.of(mesaDisponivel));
        when(mesaRepository.save(any(Mesa.class))).thenReturn(mesaDisponivel);

        Mesa resultado = mesaService.ocuparMesa(1);

        assertEquals(StatusMesa.OCUPADA, resultado.getStatus());
        verify(mesaRepository, times(1)).save(mesaDisponivel);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar ocupar mesa já ocupada")
    void deveLancarExcecaoAoTentarOcuparMesaOcupada() {
        when(mesaRepository.findByNumero(2)).thenReturn(Optional.of(mesaOcupada));

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> mesaService.ocuparMesa(2)
        );

        assertEquals("A mesa 2 já está ocupada.", excecao.getMessage());
        verify(mesaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve liberar mesa ocupada")
    void deveLiberarMesaOcupada() {
        when(mesaRepository.findByNumero(2)).thenReturn(Optional.of(mesaOcupada));
        when(mesaRepository.save(any(Mesa.class))).thenReturn(mesaOcupada);

        Mesa resultado = mesaService.liberarMesa(2);

        assertEquals(StatusMesa.DISPONIVEL, resultado.getStatus());
        verify(mesaRepository, times(1)).save(mesaOcupada);
    }
}

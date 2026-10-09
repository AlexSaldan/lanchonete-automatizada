package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.controller;

import br.com.alexandrosaldan.lanchonete_automatizada.application.service.ComandaService;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPagamento;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPreparo;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusSessao;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.*;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ComandaController.class)
@Import(GlobalExceptionHandler.class)
class ComandaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComandaService comandaService;

    @Test
    @DisplayName("Deve abrir sessão com sucesso quando dados válidos")
    void deveAbrirSessaoComSucesso() throws Exception {
        ExtratoSubcomandaResponse subcomandaDto = new ExtratoSubcomandaResponse(
                1L, "João", StatusPagamento.PENDENTE, BigDecimal.ZERO, List.of()
        );
        ExtratoMesaResponse response = new ExtratoMesaResponse(
                1L, 1, StatusSessao.ABERTA, BigDecimal.ZERO, List.of(subcomandaDto)
        );

        when(comandaService.abrirSessao(any(AberturaSessaoRequest.class))).thenReturn(response);

        String payload = """
                {
                  "numeroMesa": 1,
                  "tokenQrCode": "abc-123-token",
                  "nomeCliente": "João",
                  "cpfCliente": "123.456.789-00"
                }
                """;

        mockMvc.perform(post("/comandas/sessao/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/comandas/sessao/1"))
                .andExpect(jsonPath("$.sessaoId").value(1))
                .andExpect(jsonPath("$.numeroMesa").value(1))
                .andExpect(jsonPath("$.statusSessao").value("ABERTA"))
                .andExpect(jsonPath("$.subcomandas", hasSize(1)))
                .andExpect(jsonPath("$.subcomandas[0].nomeCliente").value("João"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando token QR Code estiver vazio")
    void deveRetornar400QuandoTokenVazio() throws Exception {
        String payloadInvalido = """
                {
                  "numeroMesa": 1,
                  "tokenQrCode": "",
                  "nomeCliente": "João"
                }
                """;

        mockMvc.perform(post("/comandas/sessao/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    @DisplayName("Deve retornar 400 quando nome do cliente estiver vazio")
    void deveRetornar400QuandoNomeVazio() throws Exception {
        String payloadInvalido = """
                {
                  "numeroMesa": 1,
                  "tokenQrCode": "abc-123",
                  "nomeCliente": ""
                }
                """;

        mockMvc.perform(post("/comandas/sessao/abrir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    @DisplayName("Deve lançar item com sucesso")
    void deveLancarItemComSucesso() throws Exception {
        ItemPedidoResponse itemResponse = new ItemPedidoResponse(
                1L, "X-Bacon", 2, new BigDecimal("22.90"),
                new BigDecimal("45.80"), "Sem cebola", StatusPreparo.RECEBIDO
        );

        when(comandaService.lancarItem(any(ItemPedidoRequest.class))).thenReturn(itemResponse);

        String payload = """
                {
                  "subcomandaId": 1,
                  "produtoId": 5,
                  "quantidade": 2,
                  "observacao": "Sem cebola"
                }
                """;

        mockMvc.perform(post("/comandas/item/lancar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/comandas/item/1"))
                .andExpect(jsonPath("$.nomeProduto").value("X-Bacon"))
                .andExpect(jsonPath("$.quantidade").value(2))
                .andExpect(jsonPath("$.subtotal").value(45.80))
                .andExpect(jsonPath("$.observacao").value("Sem cebola"))
                .andExpect(jsonPath("$.statusPreparo").value("RECEBIDO"));
    }

    @Test
    @DisplayName("Deve retornar 400 quando quantidade for zero")
    void deveRetornar400QuandoQuantidadeZero() throws Exception {
        String payloadInvalido = """
                {
                  "subcomandaId": 1,
                  "produtoId": 5,
                  "quantidade": 0
                }
                """;

        mockMvc.perform(post("/comandas/item/lancar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    @DisplayName("Deve buscar extrato da mesa com sucesso")
    void deveBuscarExtratoMesaComSucesso() throws Exception {
        ExtratoSubcomandaResponse subcomandaDto = new ExtratoSubcomandaResponse(
                1L, "João", StatusPagamento.PENDENTE, new BigDecimal("45.80"), List.of()
        );
        ExtratoMesaResponse response = new ExtratoMesaResponse(
                1L, 1, StatusSessao.ABERTA, new BigDecimal("45.80"), List.of(subcomandaDto)
        );

        when(comandaService.buscarExtratoMesa(1L)).thenReturn(response);

        mockMvc.perform(get("/comandas/sessao/1/extrato")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessaoId").value(1))
                .andExpect(jsonPath("$.numeroMesa").value(1))
                .andExpect(jsonPath("$.valorTotalMesa").value(45.80))
                .andExpect(jsonPath("$.subcomandas", hasSize(1)));
    }

    @Test
    @DisplayName("Deve adicionar cliente a sessão existente")
    void deveAdicionarClienteASessao() throws Exception {
        ExtratoSubcomandaResponse response = new ExtratoSubcomandaResponse(
                2L, "Maria", StatusPagamento.PENDENTE, BigDecimal.ZERO, List.of()
        );

        when(comandaService.adicionarCliente(any(AdicionarClienteRequest.class))).thenReturn(response);

        String payload = """
                {
                  "sessaoId": 1,
                  "nomeCliente": "Maria",
                  "cpfCliente": "987.654.321-00"
                }
                """;

        mockMvc.perform(post("/comandas/subcomanda/adicionar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/comandas/subcomanda/2"))
                .andExpect(jsonPath("$.subcomandaId").value(2))
                .andExpect(jsonPath("$.nomeCliente").value("Maria"))
                .andExpect(jsonPath("$.statusPagamento").value("PENDENTE"));
    }
}


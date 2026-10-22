package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.alexandrosaldan.lanchonete_automatizada.application.service.CozinhaService;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPreparo;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.FilaCozinhaResponse;

@WebMvcTest(CozinhaController.class)
class CozinhaControllerWebMvcTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private CozinhaService cozinhaService;

	@Test
	void deveIniciarPreparoERetornar204() throws Exception {
		mockMvc.perform(patch("/cozinha/itens/1/iniciar")).andExpect(status().isNoContent());

		verify(cozinhaService).iniciarPreparo(1L);
	}

	@Test
	void deveMarcarPedidoComoProntoERetornar204() throws Exception {

		mockMvc.perform(patch("/cozinha/itens/1/pronto")).andExpect(status().isNoContent());

		verify(cozinhaService).marcarComoPronto(1L);
	}

	@Test
	void deveEntregarPedidoERetornar204() throws Exception {

		mockMvc.perform(patch("/cozinha/itens/1/entregar")).andExpect(status().isNoContent());

		verify(cozinhaService).marcarComoEntregue(1L);
	}

	@Test
	void deveConsultarFilaERetornar200() throws Exception {

		FilaCozinhaResponse pedido = new FilaCozinhaResponse(1L, "Hambúrguer", 2, "Sem cebola", StatusPreparo.RECEBIDO,
				LocalDateTime.of(2026, 10, 21, 8, 30));

		when(cozinhaService.consultarFila()).thenReturn(List.of(pedido));

		mockMvc.perform(get("/cozinha/fila")).andExpect(status().isOk()).andExpect(jsonPath("$[0].itemId").value(1))
				.andExpect(jsonPath("$[0].nomeProduto").value("Hambúrguer"))
				.andExpect(jsonPath("$[0].quantidade").value(2))
				.andExpect(jsonPath("$[0].observacao").value("Sem cebola"))
				.andExpect(jsonPath("$[0].statusPreparo").value("RECEBIDO"));

		verify(cozinhaService).consultarFila();
	}

	@Test
	void deveRetornarListaVaziaQuandoNaoExistiremPedidos() throws Exception {

		when(cozinhaService.consultarFila()).thenReturn(List.of());

		mockMvc.perform(get("/cozinha/fila")).andExpect(status().isOk()).andExpect(jsonPath("$").isEmpty());

		verify(cozinhaService).consultarFila();
	}
}

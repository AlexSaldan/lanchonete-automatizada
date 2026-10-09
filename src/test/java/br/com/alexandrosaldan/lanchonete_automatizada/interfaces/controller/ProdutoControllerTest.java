package br.com.alexandrosaldan.lanchonete_automatizada.interfaces.controller;

import br.com.alexandrosaldan.lanchonete_automatizada.application.service.ProdutoService;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProdutoController.class)
@Import(GlobalExceptionHandler.class)
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoService produtoService;

    @Test
    void deveRetornar400QuandoBeanValidationFalhar() throws Exception {
        String payloadInvalido = """
                {
                  "nome": "",
                  "descricao": "teste",
                  "categoria": "LANCHE",
                  "preco": 0,
                  "tempoPreparoMinutos": -1
                }
                """;

        mockMvc.perform(post("/produtos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Requisição Inválida"))
                .andExpect(jsonPath("$.message").value("Um ou mais campos estão inválidos"))
                .andExpect(jsonPath("$.messages", hasItem(containsString("nome:"))))
                .andExpect(jsonPath("$.messages", hasItem(containsString("preco:"))))
                .andExpect(jsonPath("$.messages", hasItem(containsString("tempoPreparoMinutos:"))));
    }
}

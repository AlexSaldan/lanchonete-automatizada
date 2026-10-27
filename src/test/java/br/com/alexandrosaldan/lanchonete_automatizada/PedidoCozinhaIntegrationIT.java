package br.com.alexandrosaldan.lanchonete_automatizada;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPreparo;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.ItemPedidoResponse;
import br.com.alexandrosaldan.lanchonete_automatizada.interfaces.dto.ItemPedidoRequest;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.SessaoMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Mesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Produto;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.CategoriaProduto;
import br.com.alexandrosaldan.lanchonete_automatizada.application.service.ComandaService;
import br.com.alexandrosaldan.lanchonete_automatizada.application.service.CozinhaService;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.MesaRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.SessaoMesaRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.SubcomandaRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.ProdutoRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.ItemPedidoRepository;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;

@SpringBootTest
@ActiveProfiles("test")
class PedidoCozinhaIntegrationIT {
	@Autowired
	private ComandaService comandaService;

	@Autowired
	private CozinhaService cozinhaService;

	@Autowired
	private MesaRepository mesaRepository;

	@Autowired
	private SessaoMesaRepository sessaoMesaRepository;

	@Autowired
	private SubcomandaRepository subcomandaRepository;

	@Autowired
	private ProdutoRepository produtoRepository;

	@Autowired
	private ItemPedidoRepository itemPedidoRepository;

	@Test
	@Transactional
	void deveEnviarPedidoDaComandaParaFilaDaCozinha() {

		Produto produto = new Produto("X-Bacon", "Hambúrguer com bacon e queijo", CategoriaProduto.LANCHE,
				new BigDecimal("22.90"), 15);
		produto = produtoRepository.save(produto);

		Mesa mesa = new Mesa(1002, 4);

		mesa = mesaRepository.save(mesa);

		SessaoMesa sessao = new SessaoMesa(mesa);

		Subcomanda subcomanda = new Subcomanda("João", null);

		sessao.adicionarSubcomanda(subcomanda);
		sessao = sessaoMesaRepository.save(sessao);
		
		Long subcomandaId = sessao.getSubcomandas()
		        .getFirst()
		        .getId();
		
		ItemPedidoRequest request = new ItemPedidoRequest(
		        subcomandaId,
		        produto.getId(),
		        2,
		        "Sem cebola"
		);
		
		ItemPedidoResponse response = comandaService.lancarItem(request);
		
		assertEquals(StatusPreparo.RECEBIDO, response.statusPreparo());
		
		var fila = cozinhaService.consultarFila();
		
		assertTrue(
			    fila.stream().anyMatch(item ->
			        item.itemId().equals(response.id())
			    )
			);

	}

}

package br.com.alexandrosaldan.lanchonete_automatizada.domain.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Mesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.SessaoMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import java.math.BigDecimal;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Produto;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.CategoriaProduto;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.ItemPedido;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.concurrent.TimeoutException;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("mysql-test")
class ItemPedidoRepositoryIT {
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private MesaRepository mesaRepository;
	
	@Autowired
	private SessaoMesaRepository sessaoMesaRepository;
	
	@Autowired
	private SubcomandaRepository subcomandaRepository;
	
	@Autowired
	private PlatformTransactionManager transactionManager;
	
	@Test
	void deveAguardarLiberacaoDoBloqueioPessimista() throws Exception {

	    TransactionTemplate transacao = new TransactionTemplate(transactionManager);
	    Long itemId = transacao.execute(status -> {
	        Mesa mesa = mesaRepository.save(new Mesa(2001, 4));
	        

	        SessaoMesa sessao = sessaoMesaRepository.save(new SessaoMesa(mesa));

	        Subcomanda subcomanda = new Subcomanda("Cliente Teste Cozinha", null);
	        subcomanda.setSessaoMesa(sessao);
	        subcomanda = subcomandaRepository.save(subcomanda);
	        Produto produto = produtoRepository.save(
	                new Produto(
	                    "X-Burger Teste",
	                    "Lanche para teste de concorrência",
	                    CategoriaProduto.LANCHE,
	                    new BigDecimal("25.00"),
	                    10
	                )
	            );

	            ItemPedido item = itemPedidoRepository.save(
	                new ItemPedido(subcomanda, produto, 1, "Sem cebola")
	            );

	            return item.getId();
	        }); 
	          
	           assertNotNull(itemId);
	           
	           CountDownLatch primeiroBloqueioAdquirido = new CountDownLatch(1);
	           CountDownLatch liberarPrimeiroBloqueio = new CountDownLatch(1);
	           CountDownLatch segundaTentativaIniciada = new CountDownLatch(1);

	           ExecutorService executor = Executors.newFixedThreadPool(2);
	           
	           try {
	        	    Future<?> primeiraTransacao = executor.submit(() ->
	        	        transacao.executeWithoutResult(status -> {

	        	            itemPedidoRepository.buscarPorIdComBloqueio(itemId)
	        	                    .orElseThrow();

	        	            primeiroBloqueioAdquirido.countDown();
	        	         

	        	            try {
	        	                if (!liberarPrimeiroBloqueio.await(10, TimeUnit.SECONDS)) {
	        	                    throw new IllegalStateException(
	        	                        "Tempo esgotado aguardando liberação do bloqueio."
	        	                    );
	        	                }
	        	            } catch (InterruptedException e) {
	        	                Thread.currentThread().interrupt();
	        	                throw new IllegalStateException(e);
	        	            }
	        	        })
	        	    );
	        	    
	        	    // A thread principal aguarda a primeira adquirir o bloqueio.
	        	    assertTrue(primeiroBloqueioAdquirido.await(5, TimeUnit.SECONDS));
	        	    
	        	    Future<?> segundaTransacao = executor.submit(() ->
	        	    transacao.executeWithoutResult(status -> {

	        	        segundaTentativaIniciada.countDown();

	        	        itemPedidoRepository.buscarPorIdComBloqueio(itemId)
	        	                .orElseThrow();
	        	    })
	        	);

	        	assertTrue(segundaTentativaIniciada.await(5, TimeUnit.SECONDS));

	        	assertThrows(
	        	    TimeoutException.class,
	        	    () -> segundaTransacao.get(500, TimeUnit.MILLISECONDS),
	        	    "A segunda transação não deveria concluir enquanto a primeira mantém o bloqueio."
	        	);
	        	    
	        	// Autoriza a primeira transação a terminar e liberar o bloqueio.
	        	liberarPrimeiroBloqueio.countDown();

	        	// Aguarda a conclusão das duas transações.
	        	primeiraTransacao.get(10, TimeUnit.SECONDS);
	        	segundaTransacao.get(10, TimeUnit.SECONDS);   

	        	} finally {
	        	    liberarPrimeiroBloqueio.countDown();
	        	    executor.shutdownNow();
	        	}
	           
	           

	}

}
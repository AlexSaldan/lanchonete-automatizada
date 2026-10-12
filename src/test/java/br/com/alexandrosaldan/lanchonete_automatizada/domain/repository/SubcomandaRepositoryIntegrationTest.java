package br.com.alexandrosaldan.lanchonete_automatizada.domain.repository;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Mesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.SessaoMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SubcomandaRepositoryIntegrationTest {

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

        Long subcomandaId = transacao.execute(status -> {
            Mesa mesa = mesaRepository.save(new Mesa(1001, 4));
            SessaoMesa sessao = sessaoMesaRepository.save(new SessaoMesa(mesa));

            Subcomanda subcomanda = new Subcomanda("Cliente Teste", null);
            subcomanda.setSessaoMesa(sessao);

            return subcomandaRepository.save(subcomanda).getId();
        });

        assertNotNull(subcomandaId);

        CountDownLatch primeiroBloqueioAdquirido = new CountDownLatch(1);
        CountDownLatch liberarPrimeiroBloqueio = new CountDownLatch(1);
        CountDownLatch segundaTentativaIniciada = new CountDownLatch(1);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<?> primeiraTransacao = executor.submit(() ->
                transacao.executeWithoutResult(status -> {
                    subcomandaRepository.buscarPorIdComBloqueio(subcomandaId)
                            .orElseThrow();

                    primeiroBloqueioAdquirido.countDown();

                    try {
                        if (!liberarPrimeiroBloqueio.await(10, TimeUnit.SECONDS)) {
                            throw new IllegalStateException("Tempo esgotado aguardando liberação.");
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException(e);
                    }
                })
            );

            assertTrue(primeiroBloqueioAdquirido.await(5, TimeUnit.SECONDS));

            Future<?> segundaTransacao = executor.submit(() ->
                transacao.executeWithoutResult(status -> {
                    segundaTentativaIniciada.countDown();

                    subcomandaRepository.buscarPorIdComBloqueio(subcomandaId)
                            .orElseThrow();
                })
            );

            assertTrue(segundaTentativaIniciada.await(5, TimeUnit.SECONDS));

            assertThrows(
                    java.util.concurrent.TimeoutException.class,
                    () -> segundaTransacao.get(500, TimeUnit.MILLISECONDS),
                    "A segunda transação não deveria concluir enquanto a primeira mantém o bloqueio."
            );

            liberarPrimeiroBloqueio.countDown();

            primeiraTransacao.get(10, TimeUnit.SECONDS);
            segundaTransacao.get(10, TimeUnit.SECONDS);

        } finally {
            liberarPrimeiroBloqueio.countDown();
            executor.shutdownNow();
        }
    }
}

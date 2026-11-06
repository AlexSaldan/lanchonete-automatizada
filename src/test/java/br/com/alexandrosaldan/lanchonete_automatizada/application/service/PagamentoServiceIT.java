package br.com.alexandrosaldan.lanchonete_automatizada.application.service;

import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Mesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.SessaoMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.entity.Subcomanda;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusMesa;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPagamento;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusSessao;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.MesaRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.SessaoMesaRepository;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.repository.SubcomandaRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("mysql-test")
class PagamentoServiceIT {

    @Autowired
    private PagamentoService pagamentoService;

    @Autowired
    private MesaRepository mesaRepository;

    @Autowired
    private SessaoMesaRepository sessaoMesaRepository;

    @Autowired
    private SubcomandaRepository subcomandaRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void deveEncerrarSessaoQuandoDoisPagamentosForemConfirmadosSimultaneamente()
            throws Exception {

        TransactionTemplate transacao = new TransactionTemplate(transactionManager);

        Long[] ids = transacao.execute(_ -> {

            Mesa mesa = new Mesa(100000 + java.util.concurrent.ThreadLocalRandom.current().nextInt(1000000), 4);
            mesa.setStatus(StatusMesa.OCUPADA);
            mesa = mesaRepository.save(mesa);

            SessaoMesa sessao = new SessaoMesa(mesa);

            Subcomanda joao = new Subcomanda("João", null);
            joao.setValorTotal(new BigDecimal("45.80"));
            joao.setStatusPagamento(StatusPagamento.PROCESSANDO);

            Subcomanda maria = new Subcomanda("Maria", null);
            maria.setValorTotal(new BigDecimal("30.00"));
            maria.setStatusPagamento(StatusPagamento.PROCESSANDO);

            sessao.adicionarSubcomanda(joao);
            sessao.adicionarSubcomanda(maria);

            sessao = sessaoMesaRepository.save(sessao);

            return new Long[] {
                    mesa.getId(),
                    sessao.getId(),
                    joao.getId(),
                    maria.getId()
            };
        });

        assertNotNull(ids);

        CountDownLatch inicio = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Future<?> pagamentoJoao = executor.submit(() -> {
                aguardarInicio(inicio);
                pagamentoService.confirmarPagamento(ids[2]);
            });

            Future<?> pagamentoMaria = executor.submit(() -> {
                aguardarInicio(inicio);
                pagamentoService.confirmarPagamento(ids[3]);
            });

            inicio.countDown();

            pagamentoJoao.get(15, TimeUnit.SECONDS);
            pagamentoMaria.get(15, TimeUnit.SECONDS);

        } finally {
            inicio.countDown();
            executor.shutdownNow();
        }

        transacao.executeWithoutResult(_ -> {

            Subcomanda joao = subcomandaRepository.findById(ids[2])
                    .orElseThrow();

            Subcomanda maria = subcomandaRepository.findById(ids[3])
                    .orElseThrow();

            SessaoMesa sessao = sessaoMesaRepository.findById(ids[1])
                    .orElseThrow();

            Mesa mesa = mesaRepository.findById(ids[0])
                    .orElseThrow();

            assertEquals(StatusPagamento.PAGO, joao.getStatusPagamento());
            assertEquals(StatusPagamento.PAGO, maria.getStatusPagamento());

            assertEquals(StatusSessao.ENCERRADA, sessao.getStatus());
            assertEquals(StatusMesa.DISPONIVEL, mesa.getStatus());
        });
    }

    private void aguardarInicio(CountDownLatch inicio) {
        try {
            if (!inicio.await(10, TimeUnit.SECONDS)) {
                throw new IllegalStateException(
                        "Tempo esgotado aguardando início dos pagamentos.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}

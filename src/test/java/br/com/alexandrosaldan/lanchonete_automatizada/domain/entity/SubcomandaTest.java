package br.com.alexandrosaldan.lanchonete_automatizada.domain.entity;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import br.com.alexandrosaldan.lanchonete_automatizada.domain.enums.StatusPagamento;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class SubcomandaTest {

	@Test
	void devePermitirEncerramentoQuandoSubcomandaNaoTemConsumo() {
		// Arrange: prepara uma subcomanda sem consumo
		Subcomanda subcomanda = new Subcomanda("Cliente Teste", null);

		// Act: executa a regra de negócio
		boolean resultado = subcomanda.estaRegularizadaParaEncerramento();

		// Assert: verifica o resultado esperado
		assertTrue(resultado);
	}

	@Test
	void naoDevePermitirEncerramentoQuandoExistePagamentoPendente() {
		// Arrange: cria uma subcomanda com dívida de R$ 50,00
		Subcomanda subcomanda = new Subcomanda("Cliente Teste", null);
		subcomanda.setValorTotal(new BigDecimal("50.00"));

		// Act: verifica se a subcomanda está regularizada
		boolean resultado = subcomanda.estaRegularizadaParaEncerramento();

		// Assert: a dívida deve impedir o encerramento
		assertFalse(resultado);
	}

	@Test
	void devePermitirEncerramentoQuandoPagamentoEstaConfirmado() {
		// Arrange: cria uma subcomanda com consumo de R$ 50,00
		Subcomanda subcomanda = new Subcomanda("Cliente Teste", null);
		subcomanda.setValorTotal(new BigDecimal("50.00"));
		subcomanda.setStatusPagamento(StatusPagamento.PAGO);

		// Act: verifica se a subcomanda está regularizada
		boolean resultado = subcomanda.estaRegularizadaParaEncerramento();

		// Assert: pagamento confirmado permite encerramento
		assertTrue(resultado);
	}

	@Test
	void naoDevePermitirEncerramentoQuandoPagamentoEstaProcessando() {
		// Arrange: cria uma subcomanda com pagamento em andamento
		Subcomanda subcomanda = new Subcomanda("Cliente Teste", null);
		subcomanda.setValorTotal(new BigDecimal("50.00"));
		subcomanda.setStatusPagamento(StatusPagamento.PROCESSANDO);

		// Act: verifica a situação financeira
		boolean resultado = subcomanda.estaRegularizadaParaEncerramento();

		// Assert: pagamento em processamento deve impedir encerramento
		assertFalse(resultado);
	}

	@Test
	void naoDevePermitirEncerramentoQuandoPagamentoEstaProcessandoMesmoSemValor() {
		// Arrange: cria uma subcomanda sem consumo
		Subcomanda subcomanda = new Subcomanda("Cliente Teste", null);
		subcomanda.setStatusPagamento(StatusPagamento.PROCESSANDO);

		// Act: verifica a situação financeira
		boolean resultado = subcomanda.estaRegularizadaParaEncerramento();

		// Assert: pagamento em processamento deve bloquear o encerramento
		assertFalse(resultado);
	}

}

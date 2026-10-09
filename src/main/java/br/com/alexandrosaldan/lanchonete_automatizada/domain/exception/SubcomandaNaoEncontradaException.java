package br.com.alexandrosaldan.lanchonete_automatizada.domain.exception;

public class SubcomandaNaoEncontradaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public SubcomandaNaoEncontradaException(String message) {
        super(message);
    }
}

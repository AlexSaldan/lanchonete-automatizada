package br.com.alexandrosaldan.lanchonete_automatizada.domain.exception;

public class SessaoNaoEncontradaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public SessaoNaoEncontradaException(String message) {
        super(message);
    }
}

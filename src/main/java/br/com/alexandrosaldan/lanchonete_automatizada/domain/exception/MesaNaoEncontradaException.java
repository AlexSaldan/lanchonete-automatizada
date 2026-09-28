package br.com.alexandrosaldan.lanchonete_automatizada.domain.exception;

public class MesaNaoEncontradaException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public MesaNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}

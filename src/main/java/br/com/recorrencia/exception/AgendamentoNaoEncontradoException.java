package br.com.recorrencia.exception;

public class AgendamentoNaoEncontradoException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AgendamentoNaoEncontradoException(Long id) {
        super("Agendamento " + id + " não encontrado");
    }
}

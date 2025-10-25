package br.com.recorrencia.exception;


public class FalhaIrrecuperavelIntegracaoException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FalhaIrrecuperavelIntegracaoException(String message) {
        super(message);
    }

    public FalhaIrrecuperavelIntegracaoException(String message, Throwable cause) {
        super(message, cause);
    }
}

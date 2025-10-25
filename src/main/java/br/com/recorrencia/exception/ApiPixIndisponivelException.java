package br.com.recorrencia.exception;

public class ApiPixIndisponivelException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ApiPixIndisponivelException(String message) {
        super(message);
    }

    public ApiPixIndisponivelException(String message, Throwable cause) {
        super(message, cause);
    }
}

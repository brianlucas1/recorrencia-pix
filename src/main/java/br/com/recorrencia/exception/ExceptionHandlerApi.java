package br.com.recorrencia.exception;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.recorrencia.dto.RespostaErro;
import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionHandlerApi {

	private ResponseEntity<RespostaErro> construirRespostaErro(HttpStatus status, String mensagem, String caminho,
			List<String> detalhes) {
		RespostaErro resposta = new RespostaErro(OffsetDateTime.now(), status.value(), status.getReasonPhrase(),
				mensagem, caminho, detalhes);
		return ResponseEntity.status(status).body(resposta);
	}

	@ExceptionHandler(AgendamentoNaoEncontradoException.class)
	public ResponseEntity<RespostaErro> tratarAgendamentoNaoEncontrado(AgendamentoNaoEncontradoException excecao,
			HttpServletRequest requisicao) {
		return construirRespostaErro(HttpStatus.NOT_FOUND, excecao.getMessage(), requisicao.getRequestURI(), List.of());
	}

	 @ExceptionHandler(Exception.class)
	public ResponseEntity<RespostaErro> tratarErroGenerico(Exception excecao, HttpServletRequest requisicao) {
		return construirRespostaErro(HttpStatus.INTERNAL_SERVER_ERROR, excecao.getMessage(), requisicao.getRequestURI(),
				List.of());
	}
	 
	    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
	    public ResponseEntity<RespostaErro> tratarErrosValidacao(Exception excecao, HttpServletRequest requisicao) {
	        List<String> detalhes;
	        if (excecao instanceof MethodArgumentNotValidException argumentoInvalido) {
	            detalhes = argumentoInvalido.getBindingResult().getFieldErrors().stream()
	                    .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
	                    .collect(Collectors.toList());
	        } else if (excecao instanceof BindException bindException) {
	            detalhes = bindException.getBindingResult().getFieldErrors().stream()
	                    .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
	                    .collect(Collectors.toList());
	        } else {
	            detalhes = List.of(excecao.getMessage());
	        }
	        return construirRespostaErro(HttpStatus.BAD_REQUEST, "Erro de validação", requisicao.getRequestURI(), detalhes);
	    }

}

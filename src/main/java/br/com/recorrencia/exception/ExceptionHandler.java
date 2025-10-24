package br.com.recorrencia.exception;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.recorrencia.dto.RespostaErro;

@RestControllerAdvice
public class ExceptionHandler {
	
	 private ResponseEntity<RespostaErro> construirRespostaErro(
	            HttpStatus status, String mensagem, String caminho, List<String> detalhes) {
	        RespostaErro resposta = new RespostaErro(
	                OffsetDateTime.now(), status.value(), status.getReasonPhrase(), mensagem, caminho, detalhes);
	        return ResponseEntity.status(status).body(resposta);
	    }

}

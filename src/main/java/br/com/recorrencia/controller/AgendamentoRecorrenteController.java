package br.com.recorrencia.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.recorrencia.dto.RequestAgendamentoDTO;
import br.com.recorrencia.dto.ResponseAgendamentoDTO;
import br.com.recorrencia.dto.ResponseCriacaoAgendamentoDTO;
import br.com.recorrencia.service.AgendamentoRecorrenteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/agendamentos")
public class AgendamentoRecorrenteController {

	
	private final AgendamentoRecorrenteService agendamentoService;
	
	public AgendamentoRecorrenteController(AgendamentoRecorrenteService agendamentoService) {
		this.agendamentoService = agendamentoService;
	}
	
	
	@PostMapping
    public ResponseEntity<ResponseAgendamentoDTO> criar(
            @Valid @RequestBody RequestAgendamentoDTO requisicao,
            @RequestHeader(value = "X-Idempotency-Key", required = false) String chaveIdempotencia) {
        ResponseCriacaoAgendamentoDTO resultado = agendamentoService.criar(requisicao, chaveIdempotencia);
        HttpStatus status = resultado.criado() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(resultado.resposta());
    }
	
	@GetMapping("/{id}")
    public ResponseEntity<ResponseAgendamentoDTO> buscarPorId(@PathVariable Long id) {
		ResponseAgendamentoDTO resposta = agendamentoService.buscarPorId(id);
        return ResponseEntity.ok(resposta);
    }
	
}

package br.com.recorrencia.service;

import org.springframework.stereotype.Service;

import br.com.recorrencia.dto.RequestAgendamentoDTO;
import br.com.recorrencia.dto.ResponseAgendamentoDTO;
import br.com.recorrencia.dto.ResponseCriacaoAgendamentoDTO;
import jakarta.validation.Valid;

@Service
public class AgendamentoRecorrenteService {

	public ResponseCriacaoAgendamentoDTO criar(@Valid RequestAgendamentoDTO requisicao, String chaveIdempotencia) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResponseAgendamentoDTO buscarPorId(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

}

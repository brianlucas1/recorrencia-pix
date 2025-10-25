package br.com.recorrencia.interfaces;

import java.util.Optional;

import br.com.recorrencia.dto.ContextoAvaliacaoFraudeDTO;
import br.com.recorrencia.dto.ResultadoFraudeDTO;

public interface RegraFraude {

    Optional<ResultadoFraudeDTO> avaliar(ContextoAvaliacaoFraudeDTO contexto);

	
}

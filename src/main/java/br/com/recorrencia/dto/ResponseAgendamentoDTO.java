package br.com.recorrencia.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import br.com.recorrencia.enums.PeriodicidadeEnum;
import br.com.recorrencia.enums.StatusAgendamentoEnum;
import br.com.recorrencia.enums.StatusDecisaoFraudeEnum;

public record ResponseAgendamentoDTO(
		 Long id,
	        String referenciaExterna,
	        String documentoPagador,
	        String nomePagador,
	        String documentoRecebedor,
	        String nomeRecebedor,
	        BigDecimal valor,
	        PeriodicidadeEnum periodicidade,
	        OffsetDateTime primeiraExecucao,
	        StatusAgendamentoEnum status,
	        StatusDecisaoFraudeEnum statusFraude,
	        String motivoRisco,
	        OffsetDateTime criadoEm,
	        OffsetDateTime atualizadoEm,
	        String descricao) {}


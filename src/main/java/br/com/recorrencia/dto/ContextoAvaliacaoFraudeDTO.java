package br.com.recorrencia.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import br.com.recorrencia.enums.PeriodicidadeEnum;

public record ContextoAvaliacaoFraudeDTO(
        String documentoPagador,
        String documentoRecebedor,
        BigDecimal valor,
        PeriodicidadeEnum periodicidade,
        OffsetDateTime primeiraExecucao) {}
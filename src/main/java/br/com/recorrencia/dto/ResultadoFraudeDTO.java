package br.com.recorrencia.dto;

import br.com.recorrencia.enums.StatusDecisaoFraudeEnum;

public record ResultadoFraudeDTO(StatusDecisaoFraudeEnum status, String motivo, String regra) {}

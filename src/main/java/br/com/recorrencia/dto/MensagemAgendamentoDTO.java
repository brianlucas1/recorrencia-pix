package br.com.recorrencia.dto;

public record MensagemAgendamentoDTO(
        Long agendamentoId,
        String referenciaExterna,
        String status,
        String statusFraude,
        String motivoRisco) {}

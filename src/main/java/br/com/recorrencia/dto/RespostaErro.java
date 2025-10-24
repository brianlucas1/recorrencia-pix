package br.com.recorrencia.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record RespostaErro(
        OffsetDateTime dataHora,
        int status,
        String erro,
        String mensagem,
        String caminho,
        List<String> detalhes) {}
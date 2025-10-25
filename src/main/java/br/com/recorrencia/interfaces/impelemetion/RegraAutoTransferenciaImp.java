package br.com.recorrencia.interfaces.impelemetion;

import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.recorrencia.dto.ContextoAvaliacaoFraudeDTO;
import br.com.recorrencia.dto.ResultadoFraudeDTO;
import br.com.recorrencia.enums.StatusDecisaoFraudeEnum;
import br.com.recorrencia.interfaces.RegraFraude;

@Component
public class RegraAutoTransferenciaImp implements RegraFraude {

    @Override
    public Optional<ResultadoFraudeDTO> avaliar(ContextoAvaliacaoFraudeDTO contexto) {
        if (contexto.documentoPagador().equals(contexto.documentoRecebedor())) {
            return Optional.of(new ResultadoFraudeDTO(
                    StatusDecisaoFraudeEnum.REJEITADO,
                    "Pagador e recebedor possuem o mesmo documento",
                    "RegraAutoTransferencia"));
        }
        return Optional.empty();
    }
}

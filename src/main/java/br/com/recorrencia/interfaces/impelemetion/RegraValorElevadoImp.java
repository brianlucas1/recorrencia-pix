package br.com.recorrencia.interfaces.impelemetion;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.recorrencia.dto.ContextoAvaliacaoFraudeDTO;
import br.com.recorrencia.dto.ResultadoFraudeDTO;
import br.com.recorrencia.enums.StatusDecisaoFraudeEnum;
import br.com.recorrencia.interfaces.RegraFraude;

@Component
public class RegraValorElevadoImp implements RegraFraude {

    private static final BigDecimal VALOR_MAXIMO_DIARIO = BigDecimal.valueOf(10000);

    @Override
    public Optional<ResultadoFraudeDTO> avaliar(ContextoAvaliacaoFraudeDTO contexto) {
        if (contexto.valor().compareTo(VALOR_MAXIMO_DIARIO) > 0) {
            return Optional.of(new ResultadoFraudeDTO(
                    StatusDecisaoFraudeEnum.REJEITADO,
                    "Valor acima do limite diário autorizado",
                    "RegraValorElevado"));
        }
        return Optional.empty();
    }
}

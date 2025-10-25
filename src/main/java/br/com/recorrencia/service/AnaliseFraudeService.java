package br.com.recorrencia.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import br.com.recorrencia.dto.ContextoAvaliacaoFraudeDTO;
import br.com.recorrencia.dto.ResultadoFraudeDTO;
import br.com.recorrencia.enums.StatusDecisaoFraudeEnum;
import br.com.recorrencia.interfaces.RegraFraude;

@Service
public class AnaliseFraudeService {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(AnaliseFraudeService.class);

    private final List<RegraFraude> regras;

    public AnaliseFraudeService(List<RegraFraude> regras) {
        this.regras = regras;
    }

    public ResultadoFraudeDTO avaliar(ContextoAvaliacaoFraudeDTO contexto) {
        StatusDecisaoFraudeEnum statusFinal = StatusDecisaoFraudeEnum.APROVADO;
        Optional<ResultadoFraudeDTO> resultadoSelecionado = Optional.empty();

        for (RegraFraude regra : regras) {
            Optional<ResultadoFraudeDTO> resultado = regra.avaliar(contexto);
            if (resultado.isPresent()) {
                ResultadoFraudeDTO retorno = resultado.get();
                LOGGER.info("Fraude: regra {} retornou status {} para pagador {}",
                        retorno.regra(), retorno.status(), contexto.documentoPagador());
                if (retorno.status().maisSeveroQue(statusFinal)) {
                    statusFinal = retorno.status();
                    resultadoSelecionado = Optional.of(retorno);
                } else if (retorno.status() == statusFinal && resultadoSelecionado.isPresent()) {
                    resultadoSelecionado = Optional.of(List.of(resultadoSelecionado.get(), retorno).stream()
                            .sorted(Comparator.comparing(ResultadoFraudeDTO::regra))
                            .findFirst()
                            .orElse(retorno));
                }
                if (statusFinal == StatusDecisaoFraudeEnum.REJEITADO) {
                    break;
                }
            } else {
                LOGGER.debug(
                        "Fraude: regra {} aprovada para pagador {}",
                        regra.getClass().getSimpleName(),
                        contexto.documentoPagador());
            }
        }

        return resultadoSelecionado.orElse(new ResultadoFraudeDTO(
                StatusDecisaoFraudeEnum.APROVADO, "Transação aprovada automaticamente", "AprovacaoPadrao"));
    }

}

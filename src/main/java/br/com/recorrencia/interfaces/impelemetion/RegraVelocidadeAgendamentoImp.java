package br.com.recorrencia.interfaces.impelemetion;


import java.util.EnumSet;
import java.util.Optional;

import org.springframework.stereotype.Component;

import br.com.recorrencia.dto.ContextoAvaliacaoFraudeDTO;
import br.com.recorrencia.dto.ResultadoFraudeDTO;
import br.com.recorrencia.enums.StatusAgendamentoEnum;
import br.com.recorrencia.enums.StatusDecisaoFraudeEnum;
import br.com.recorrencia.interfaces.RegraFraude;
import br.com.recorrencia.repository.AgendamentoRecorrenteRepository;

@Component
public class RegraVelocidadeAgendamentoImp implements RegraFraude {

    private final AgendamentoRecorrenteRepository repositorio;

    public RegraVelocidadeAgendamentoImp(AgendamentoRecorrenteRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Optional<ResultadoFraudeDTO> avaliar(ContextoAvaliacaoFraudeDTO contexto) {
        long agendamentosAtivos = repositorio.countByDocumentoPagadorAndStatusIn(
                contexto.documentoPagador(), EnumSet.of(StatusAgendamentoEnum.AGENDADO, StatusAgendamentoEnum.AGUARDANDO_REVISAO));
        if (agendamentosAtivos >= 3) {
            return Optional.of(new ResultadoFraudeDTO(
                    StatusDecisaoFraudeEnum.REVISAO_MANUAL,
                    "Pagador possui múltiplos agendamentos ativos em análise",
                    "RegraVelocidadeAgendamento"));
        }
        return Optional.empty();
    }
}

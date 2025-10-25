package br.com.recorrencia.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import br.com.recorrencia.dto.ContextoAvaliacaoFraudeDTO;
import br.com.recorrencia.dto.RequestAgendamentoDTO;
import br.com.recorrencia.dto.ResponseAgendamentoDTO;
import br.com.recorrencia.dto.ResponseCriacaoAgendamentoDTO;
import br.com.recorrencia.dto.ResultadoFraudeDTO;
import br.com.recorrencia.enums.StatusAgendamentoEnum;
import br.com.recorrencia.enums.StatusDecisaoFraudeEnum;
import br.com.recorrencia.exception.AgendamentoNaoEncontradoException;
import br.com.recorrencia.model.AgendamentoRecorrente;
import br.com.recorrencia.rabbit.EventoAgendamentoPublicador;
import br.com.recorrencia.repository.AgendamentoRecorrenteRepository;
import jakarta.validation.Valid;

@Service
public class AgendamentoRecorrenteService {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(AgendamentoRecorrenteService.class);

	
	private final AgendamentoRecorrenteRepository agendamentoRepo;
	private final AnaliseFraudeService analiseFraudeService;
	private final EventoAgendamentoPublicador eventoAgendamento;

	public AgendamentoRecorrenteService(AgendamentoRecorrenteRepository agendamentoRepo,
			AnaliseFraudeService analiseFraudeService, EventoAgendamentoPublicador eventoAgendamento) {
		this.agendamentoRepo = agendamentoRepo;
		this.analiseFraudeService = analiseFraudeService;
		this.eventoAgendamento = eventoAgendamento;

	}

	public ResponseCriacaoAgendamentoDTO criar(@Valid RequestAgendamentoDTO requisicao, String chaveIdempotencia) {
		
		 String referencia = gerarReferencia(chaveIdempotencia);
		
		 if (StringUtils.hasText(chaveIdempotencia)) {
	            return agendamentoRepo
	                    .findByReferenciaExterna(referencia)
	                    .map(agendamento -> {
	                        LOGGER.info(
	                                "Requisição idempotente para referência {} reutilizando agendamento {}",
	                                referencia,
	                                agendamento.getId());
	                        return new ResponseCriacaoAgendamentoDTO(paraResposta(agendamento), false);
	                    })
	                    .orElseGet(() -> criarNovoAgendamento(requisicao, referencia, true));
	        }

	        return criarNovoAgendamento(requisicao, referencia, false);
	}

	private ResponseCriacaoAgendamentoDTO criarNovoAgendamento(@Valid RequestAgendamentoDTO requisicao,
			String referencia, boolean chaveFornecida) {
		 AgendamentoRecorrente agendamento = paraEntidade(requisicao, referencia);
	        ResultadoFraudeDTO resultado = analiseFraudeService.avaliar(new ContextoAvaliacaoFraudeDTO(
	                agendamento.getDocumentoPagador(),
	                agendamento.getDocumentoRecebedor(),
	                agendamento.getValor(),
	                agendamento.getPeriodicidade(),
	                agendamento.getPrimeiraExecucao()));

	        aplicarDecisaoFraude(agendamento, resultado);
	        LOGGER.info(
	                "Resultado antifraude para referência {}: status={} motivo={}",
	                referencia,
	                resultado.status(),
	                resultado.motivo());
	        try {
	            AgendamentoRecorrente salvo = agendamentoRepo.save(agendamento);
	            eventoAgendamento.publicarCriacao(salvo);
	            return new ResponseCriacaoAgendamentoDTO(paraResposta(salvo), true);
	        } catch (DataIntegrityViolationException e) {
	            if (chaveFornecida) {
	                return agendamentoRepo
	                        .findByReferenciaExterna(referencia)
	                        .map(existente -> new ResponseCriacaoAgendamentoDTO(paraResposta(existente), false))
	                        .orElseThrow(() -> e);
	            }
	            throw e;
	        }
	}

	private void aplicarDecisaoFraude(AgendamentoRecorrente agendamento, ResultadoFraudeDTO resultado) {
		 agendamento.setStatusFraude(resultado.status());
	        agendamento.setMotivoRisco(resultado.motivo());
	        if (resultado.status() == StatusDecisaoFraudeEnum.APROVADO) {
	            agendamento.setStatus(StatusAgendamentoEnum.AGENDADO);
	        } else if (resultado.status() == StatusDecisaoFraudeEnum.REVISAO_MANUAL) {
	            agendamento.setStatus(StatusAgendamentoEnum.AGUARDANDO_REVISAO);
	        } else {
	            agendamento.setStatus(StatusAgendamentoEnum.REJEITADO);
	        }
		
	}

	private AgendamentoRecorrente paraEntidade(@Valid RequestAgendamentoDTO requisicao, String referencia) {
		   return AgendamentoRecorrente.builder()
	                .referenciaExterna(referencia)
	                .documentoPagador(requisicao.documentoPagador())
	                .documentoRecebedor(requisicao.documentoRecebedor())
	                .nomePagador(requisicao.nomePagador())
	                .nomeRecebedor(requisicao.nomeRecebedor())
	                .descricao(requisicao.descricao())
	                .valor(requisicao.valor())
	                .periodicidade(requisicao.periodicidade())
	                .primeiraExecucao(requisicao.primeiraExecucao())
	                .statusFraude(StatusDecisaoFraudeEnum.APROVADO)
	                .status(StatusAgendamentoEnum.AGENDADO)
	                .motivoRisco("Aguardando análise de fraude")
	                .build();
	}

	public ResponseAgendamentoDTO buscarPorId(Long id) {
		AgendamentoRecorrente agendamento =
				agendamentoRepo.findById(id).orElseThrow(() -> new AgendamentoNaoEncontradoException(id));
		 return paraResposta(agendamento);
	}
	
	 private String gerarReferencia(String chaveIdempotencia) {
	        if (StringUtils.hasText(chaveIdempotencia)) {
	            return chaveIdempotencia.trim();
	        }
	        return UUID.randomUUID().toString();
	    }
	 
	 private ResponseAgendamentoDTO paraResposta(AgendamentoRecorrente agendamento) {
	        return new ResponseAgendamentoDTO(
	                agendamento.getId(),
	                agendamento.getReferenciaExterna(),
	                agendamento.getDocumentoPagador(),
	                agendamento.getNomePagador(),
	                agendamento.getDocumentoRecebedor(),
	                agendamento.getNomeRecebedor(),
	                agendamento.getValor(),
	                agendamento.getPeriodicidade(),
	                agendamento.getPrimeiraExecucao(),
	                agendamento.getStatus(),
	                agendamento.getStatusFraude(),
	                agendamento.getMotivoRisco(),
	                agendamento.getCriadoEm(),
	                agendamento.getAtualizadoEm(),
	                agendamento.getDescricao());
	    }

}

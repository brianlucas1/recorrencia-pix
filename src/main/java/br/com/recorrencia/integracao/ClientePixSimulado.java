package br.com.recorrencia.integracao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import br.com.recorrencia.dto.MensagemAgendamentoDTO;
import br.com.recorrencia.exception.ApiPixIndisponivelException;
import br.com.recorrencia.exception.FalhaIrrecuperavelIntegracaoException;
import br.com.recorrencia.interfaces.IntegracaoPixService;

@Component
public class ClientePixSimulado implements IntegracaoPixService {

	 private static final Logger LOGGER = LoggerFactory.getLogger(ClientePixSimulado.class);

	    private final boolean simularFalhaApi;
	    private final String padraoErroIrrecuperavel;

	    public ClientePixSimulado(
	            @Value("${pix.integracao.simular-falha-api:false}") boolean simularFalhaApi,
	            @Value("${pix.integracao.padrao-erro-irrecuperavel:}") String padraoErroIrrecuperavel) {
	        this.simularFalhaApi = simularFalhaApi;
	        this.padraoErroIrrecuperavel = padraoErroIrrecuperavel;
	    }

	    @Override
	    public void dispararOrdemPagamento(MensagemAgendamentoDTO mensagem) {
	        if (StringUtils.hasText(padraoErroIrrecuperavel)
	                && mensagem.referenciaExterna() != null
	                && mensagem.referenciaExterna().startsWith(padraoErroIrrecuperavel)) {
	            throw new FalhaIrrecuperavelIntegracaoException(
	                    "Falha irrecuperável simulada para a referência " + mensagem.referenciaExterna());
	        }

	        if (simularFalhaApi && mensagem.agendamentoId() != null && mensagem.agendamentoId() % 2 == 0) {
	            throw new ApiPixIndisponivelException("API Pix indisponível de forma simulada");
	        }

	        LOGGER.info(
	                "Pix recorrente {} enviado com sucesso para processamento externo (referência: {}, statusFraude: {})",
	                mensagem.agendamentoId(),
	                mensagem.referenciaExterna(),
	                mensagem.statusFraude());
	    }
}

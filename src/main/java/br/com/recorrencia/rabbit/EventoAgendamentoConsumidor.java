package br.com.recorrencia.rabbit;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.rabbitmq.client.Channel;

import br.com.recorrencia.dto.MensagemAgendamentoDTO;
import br.com.recorrencia.exception.ApiPixIndisponivelException;
import br.com.recorrencia.exception.FalhaIrrecuperavelIntegracaoException;
import br.com.recorrencia.interfaces.IntegracaoPixService;

@Component
public class EventoAgendamentoConsumidor {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(EventoAgendamentoConsumidor.class);
    private static final String HEADER_RETRY_COUNT = "x-retry-count";

    private final IntegracaoPixService servicoIntegracaoPix;
    private final RabbitTemplate rabbitTemplate;
    private final String retryExchange;
    private final String retryRoutingKey;
    private final String deadLetterExchange;
    private final String deadLetterRoutingKey;
    private final int maxRetryAttempts;
    
    public EventoAgendamentoConsumidor(
    		IntegracaoPixService servicoIntegracaoPix,
            RabbitTemplate rabbitTemplate,
            @Value("${pix.rabbitmq.retry-exchange}") String retryExchange,
            @Value("${pix.rabbitmq.retry-routing-key}") String retryRoutingKey,
            @Value("${pix.rabbitmq.dead-letter-exchange}") String deadLetterExchange,
            @Value("${pix.rabbitmq.dead-letter-routing-key}") String deadLetterRoutingKey,
            @Value("${pix.mensageria.retry.max-attempts:3}") int maxRetryAttempts) {
        this.servicoIntegracaoPix = servicoIntegracaoPix;
        this.rabbitTemplate = rabbitTemplate;
        this.retryExchange = retryExchange;
        this.retryRoutingKey = retryRoutingKey;
        this.deadLetterExchange = deadLetterExchange;
        this.deadLetterRoutingKey = deadLetterRoutingKey;
        this.maxRetryAttempts = maxRetryAttempts;
    }
    
    @RabbitListener(
            queues = "${pix.rabbitmq.schedule-queue}",
            containerFactory = "manualAckContainerFactory")
    public void consumir(@Payload MensagemAgendamentoDTO mensagem, Message rawMessage, Channel channel)
            throws IOException {
        long deliveryTag = rawMessage.getMessageProperties().getDeliveryTag();
        Map<String, Object> headers = rawMessage.getMessageProperties().getHeaders();
        int tentativaAtual = ((Number) headers.getOrDefault(HEADER_RETRY_COUNT, 0)).intValue();

        try {
            servicoIntegracaoPix.dispararOrdemPagamento(mensagem);
            channel.basicAck(deliveryTag, false);
        } catch (ApiPixIndisponivelException e) {
            manejarErroTransitorio(mensagem, tentativaAtual + 1, e);
            channel.basicAck(deliveryTag, false);
        } catch (FalhaIrrecuperavelIntegracaoException e) {
            registrarNaFilaMorta(mensagem, tentativaAtual, e);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            LOGGER.error("Erro inesperado ao processar agendamento {}. Enviando para DLQ.", mensagem.agendamentoId(), e);
            registrarNaFilaMorta(mensagem, tentativaAtual, e);
            channel.basicAck(deliveryTag, false);
        }
    }

    private void manejarErroTransitorio(MensagemAgendamentoDTO mensagem, int proximaTentativa, Exception e) {
        if (proximaTentativa > maxRetryAttempts) {
            LOGGER.warn(
                    "Agendamento {} excedeu tentativas de retry ({}). Encaminhando para DLQ.",
                    mensagem.agendamentoId(),
                    maxRetryAttempts);
            registrarNaFilaMorta(mensagem, proximaTentativa, e);
            return;
        }

        LOGGER.warn(
                "Erro transitório ao integrar agendamento {}. Enfileirando para retry (tentativa {}).",
                mensagem.agendamentoId(),
                proximaTentativa,
                e);
        rabbitTemplate.convertAndSend(retryExchange, retryRoutingKey, mensagem, message -> {
            message.getMessageProperties().setHeader(HEADER_RETRY_COUNT, proximaTentativa);
            message.getMessageProperties().setHeader("x-error", e.getMessage());
            return message;
        });
    }

    private void registrarNaFilaMorta(MensagemAgendamentoDTO mensagem, int tentativaAtual, Exception e) {
        LOGGER.error(
                "Agendamento {} encaminhado para DLQ após {} tentativas. Motivo: {}",
                mensagem.agendamentoId(),
                tentativaAtual,
                e.getMessage());
        rabbitTemplate.convertAndSend(deadLetterExchange, deadLetterRoutingKey, mensagem, message -> {
            message.getMessageProperties().setHeader(HEADER_RETRY_COUNT, tentativaAtual);
            message.getMessageProperties().setHeader("x-error", e.getMessage());
            return message;
        });
    }

}

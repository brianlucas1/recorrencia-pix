package br.com.recorrencia.rabbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.recorrencia.dto.MensagemAgendamentoDTO;
import br.com.recorrencia.model.AgendamentoRecorrente;

@Component
public class EventoAgendamentoPublicador {
	
	 private static final Logger LOGGER = LoggerFactory.getLogger(EventoAgendamentoPublicador.class);

	    private final RabbitTemplate rabbitTemplate;
	    private final String exchange;
	    private final String routingKey;

	    public EventoAgendamentoPublicador(
	            RabbitTemplate rabbitTemplate,
	            @Value("${pix.rabbitmq.schedule-exchange}") String exchange,
	            @Value("${pix.rabbitmq.schedule-routing-key}") String routingKey) {
	        this.rabbitTemplate = rabbitTemplate;
	        this.exchange = exchange;
	        this.routingKey = routingKey;
	    }

	    public void publicarCriacao(AgendamentoRecorrente agendamento) {
	        MensagemAgendamentoDTO mensagem = new MensagemAgendamentoDTO(
	                agendamento.getId(),
	                agendamento.getReferenciaExterna(),
	                agendamento.getStatus().name(),
	                agendamento.getStatusFraude().name(),
	                agendamento.getMotivoRisco());
	        rabbitTemplate.convertAndSend(exchange, routingKey, mensagem);
	        LOGGER.info("Evento de agendamento enviado para o RabbitMQ: {}", mensagem);
	    }


}

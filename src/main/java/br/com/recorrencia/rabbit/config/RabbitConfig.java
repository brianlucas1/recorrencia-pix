package br.com.recorrencia.rabbit.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange exchangeAgendamento(@Value("${pix.rabbitmq.schedule-exchange}") String nomeExchange) {
        return new TopicExchange(nomeExchange, true, false);
    }

    @Bean
    public Queue filaAgendamento(@Value("${pix.rabbitmq.schedule-queue}") String nomeFila) {
        return new Queue(nomeFila, true);
    }

    @Bean
    public Binding ligacaoAgendamento(
            TopicExchange exchangeAgendamento,
            Queue filaAgendamento,
            @Value("${pix.rabbitmq.schedule-routing-key}") String rota) {
        return BindingBuilder.bind(filaAgendamento).to(exchangeAgendamento).with(rota);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        return rabbitTemplate;
    }
}

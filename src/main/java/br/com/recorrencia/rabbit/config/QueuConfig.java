package br.com.recorrencia.rabbit.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class QueuConfig {

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter);
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory manualAckContainerFactory(
            ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jackson2JsonMessageConverter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setMessageConverter(jackson2JsonMessageConverter);
        return factory;
    }

    @Bean
    public Declarables declaracoesFilasPix(
            @Value("${pix.rabbitmq.schedule-exchange}") String scheduleExchangeName,
            @Value("${pix.rabbitmq.schedule-queue}") String scheduleQueueName,
            @Value("${pix.rabbitmq.schedule-routing-key}") String scheduleRoutingKey,
            @Value("${pix.rabbitmq.retry-exchange}") String retryExchangeName,
            @Value("${pix.rabbitmq.retry-queue}") String retryQueueName,
            @Value("${pix.rabbitmq.retry-routing-key}") String retryRoutingKey,
            @Value("${pix.rabbitmq.dead-letter-exchange}") String deadLetterExchangeName,
            @Value("${pix.rabbitmq.dead-letter-queue}") String deadLetterQueueName,
            @Value("${pix.rabbitmq.dead-letter-routing-key}") String deadLetterRoutingKey,
            @Value("${pix.mensageria.retry.delay:5000}") long retryDelay) {

        DirectExchange scheduleExchange = ExchangeBuilder.directExchange(scheduleExchangeName).durable(true).build();
        DirectExchange retryExchange = ExchangeBuilder.directExchange(retryExchangeName).durable(true).build();
        DirectExchange deadLetterExchange = ExchangeBuilder.directExchange(deadLetterExchangeName).durable(true).build();

        Queue scheduleQueue = QueueBuilder.durable(scheduleQueueName)
                .withArgument("x-dead-letter-exchange", deadLetterExchangeName)
                .withArgument("x-dead-letter-routing-key", deadLetterRoutingKey)
                .build();

        Queue retryQueue = QueueBuilder.durable(retryQueueName)
                .withArgument("x-message-ttl", retryDelay)
                .withArgument("x-dead-letter-exchange", scheduleExchangeName)
                .withArgument("x-dead-letter-routing-key", scheduleRoutingKey)
                .build();

        Queue deadLetterQueue = QueueBuilder.durable(deadLetterQueueName).build();

        Binding scheduleBinding = BindingBuilder.bind(scheduleQueue)
                .to(scheduleExchange)
                .with(scheduleRoutingKey);
        Binding retryBinding = BindingBuilder.bind(retryQueue).to(retryExchange).with(retryRoutingKey);
        Binding deadLetterBinding = BindingBuilder.bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(deadLetterRoutingKey);

        return new Declarables(
                scheduleExchange,
                retryExchange,
                deadLetterExchange,
                scheduleQueue,
                retryQueue,
                deadLetterQueue,
                scheduleBinding,
                retryBinding,
                deadLetterBinding);
    }
}
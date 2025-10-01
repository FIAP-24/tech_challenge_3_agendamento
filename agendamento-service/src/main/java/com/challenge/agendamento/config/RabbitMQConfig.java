package com.challenge.agendamento.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@ConditionalOnProperty(name = "spring.rabbitmq.host")
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    @Value("${rabbitmq.queue.name:notificacoes.queue}")
    private String queueName;

    @Value("${rabbitmq.exchange.name:notificacoes.exchange}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key:notificacao.consulta}")
    private String routingKey;

    @Value("${rabbitmq.queue.notificacoes.name}")
    private String notificacoesQueueName;

    @Value("${rabbitmq.queue.historico.name}")
    private String historicoQueueName;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue queue() {
        log.info("Configurando fila de notificacoes: {}", queueName);
        return new Queue(queueName, true); // durable = true
    }

    @Bean
    public TopicExchange exchange() {
        log.info("Configurando exchange de notificacoes: {}", exchangeName);
        return new TopicExchange(exchangeName, true, false); // durable = true, autoDelete = false
    }

    @Bean
    public Binding binding() {
        log.info("Configurando binding: {} -> {} com routing key: {}", queueName, exchangeName, routingKey);
        return BindingBuilder
                .bind(queue())
                .to(exchange())
                .with(routingKey);
    }

    // --- Beans para Notificações ---
    @Bean
    public Queue notificacoesQueue() {
        return new Queue(notificacoesQueueName, true);
    }

    @Bean
    public TopicExchange notificacoesExchange() {
        return new TopicExchange("notificacoes.exchange");
    }

    @Bean
    public Binding notificacoesBinding(Queue notificacoesQueue, TopicExchange notificacoesExchange) {
        return BindingBuilder.bind(notificacoesQueue).to(notificacoesExchange).with("notificacao.#");
    }

    // --- Beans para Histórico ---
    @Bean
    public Queue historicoQueue() {
        return new Queue(historicoQueueName, true);
    }

    @Bean
    public TopicExchange historicoExchange() {
        return new TopicExchange("historico.exchange");
    }

    @Bean
    public Binding historicoBinding(Queue historicoQueue, TopicExchange historicoExchange) {
        return BindingBuilder.bind(historicoQueue).to(historicoExchange).with("historico.#");
    }
}
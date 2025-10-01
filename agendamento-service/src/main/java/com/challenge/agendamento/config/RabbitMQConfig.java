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
}
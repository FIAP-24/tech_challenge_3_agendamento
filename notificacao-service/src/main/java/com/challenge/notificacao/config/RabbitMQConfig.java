package com.challenge.notificacao.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuracao do RabbitMQ para o servico de notificacao
 */
@Configuration
public class RabbitMQConfig {
    
    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);
    
    @Value("${rabbitmq.queue.name:notificacoes.queue}")
    private String queueName;
    
    @Value("${rabbitmq.exchange.name:notificacoes.exchange}")
    private String exchangeName;
    
    @Value("${rabbitmq.routing.key:notificacao.consulta}")
    private String routingKey;
    
    /**
     * Configuracao do MessageConverter para JSON
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    
    /**
     * Configuracao da fila de notificacoes
     */
    @Bean
    public Queue notificacoesQueue() {
        log.info("Configurando fila: {}", queueName);
        return new Queue(queueName, true); // durable = true
    }
    
    /**
     * Configuracao do exchange para notificacoes
     */
    @Bean
    public TopicExchange notificacoesExchange() {
        log.info("Configurando exchange: {}", exchangeName);
        return new TopicExchange(exchangeName, true, false); // durable = true, autoDelete = false
    }
    
    /**
     * Binding entre queue e exchange
     */
    @Bean
    public Binding notificacoesBinding() {
        log.info("Configurando binding: {} -> {} com routing key: {}", queueName, exchangeName, routingKey);
        return BindingBuilder
                .bind(notificacoesQueue())
                .to(notificacoesExchange())
                .with(routingKey);
    }
    
    /**
     * Configuracao do container factory para listeners
     */
    @Bean
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setConcurrentConsumers(3);
        factory.setMaxConcurrentConsumers(10);
        factory.setPrefetchCount(10);
        
        // Configuracoes de retry
        factory.setDefaultRequeueRejected(false);
        
        log.info("Configuracao do RabbitListenerContainerFactory concluida");
        return factory;
    }
}


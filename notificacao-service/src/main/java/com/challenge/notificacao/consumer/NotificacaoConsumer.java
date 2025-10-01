package com.challenge.notificacao.consumer;

import com.challenge.notificacao.dto.NotificacaoDTO;
import com.challenge.notificacao.service.NotificacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Consumer RabbitMQ para processar mensagens de notificacao
 */
@Component
public class NotificacaoConsumer {
    
    private static final Logger log = LoggerFactory.getLogger(NotificacaoConsumer.class);
    
    @Autowired
    private NotificacaoService notificacaoService;
    
    /**
     * Consome mensagens da fila de notificacoes
     */
    @RabbitListener(queues = "${rabbitmq.queue.name:notificacoes.queue}")
    public void processarNotificacao(NotificacaoDTO notificacao) {
        try {
            log.info("📨 Nova mensagem recebida da fila: {}", notificacao);
            
            // Validar dados da notificacao
            if (validarNotificacao(notificacao)) {
                notificacaoService.processarNotificacao(notificacao);
                log.info("✅ Notificacao processada com sucesso para paciente ID: {}", 
                        notificacao.getPacienteId());
            } else {
                log.error("❌ Notificacao invalida recebida: {}", notificacao);
            }
            
        } catch (Exception e) {
            log.error("❌ Erro ao processar notificacao: {}", e.getMessage(), e);
            // Em uma implementacao real, aqui poderia implementar retry logic ou DLQ
        }
    }
    
    /**
     * Valida se a notificacao contem os dados necessarios
     */
    private boolean validarNotificacao(NotificacaoDTO notificacao) {
        if (notificacao == null) {
            log.error("Notificacao e nula");
            return false;
        }
        
        if (notificacao.getPacienteId() == null) {
            log.error("ID do paciente e obrigatorio");
            return false;
        }
        
        if (notificacao.getMensagem() == null || notificacao.getMensagem().trim().isEmpty()) {
            log.error("Mensagem e obrigatoria");
            return false;
        }
        
        return true;
    }
}


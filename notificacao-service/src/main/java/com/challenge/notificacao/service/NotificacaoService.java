package com.challenge.notificacao.service;

import com.challenge.notificacao.config.NotificacaoProperties;
import com.challenge.notificacao.dto.NotificacaoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servico principal de notificacoes
 * Suporta multiplos modos: LOG, EMAIL, SMS, AMBOS
 */
@Service
public class NotificacaoService {
    
    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);
    
    @Autowired
    private NotificacaoProperties properties;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private SmsService smsService;
    
    /**
     * Processa uma notificacao baseada no modo configurado
     */
    public void processarNotificacao(NotificacaoDTO notificacao) {
        log.info("Processando notificacao: {}", notificacao);
        
        NotificacaoProperties.ModoNotificacao modo = properties.getModo();
        
        switch (modo) {
            case LOG:
                processarLog(notificacao);
                break;
            case EMAIL:
                processarEmail(notificacao);
                break;
            case SMS:
                processarSms(notificacao);
                break;
            case AMBOS:
                processarLog(notificacao);
                processarEmail(notificacao);
                processarSms(notificacao);
                break;
            default:
                log.warn("Modo de notificacao nao reconhecido: {}. Usando LOG como fallback.", modo);
                processarLog(notificacao);
        }
    }
    
    private void processarLog(NotificacaoDTO notificacao) {
        log.info("=== NOTIFICACAO DE CONSULTA ===");
        log.info("Consulta ID: {}", notificacao.getConsultaId());
        log.info("Paciente ID: {}", notificacao.getPacienteId());
        log.info("Mensagem: {}", notificacao.getMensagem());
        log.info("Tipo: {}", notificacao.getTipo());
        log.info("Timestamp: {}", notificacao.getTimestamp());
        log.info("===============================");
        
        log.info("SIMULACAO: Email enviado para paciente ID {} - Assunto: {}", 
                notificacao.getPacienteId(), properties.getEmail().getAssuntoPadrao());
        log.info("SIMULACAO: SMS enviado para paciente ID {} - Provedor: {}", 
                notificacao.getPacienteId(), properties.getSms().getProvedor());
    }
    
    /**
     * Processa notificacao por email
     */
    private void processarEmail(NotificacaoDTO notificacao) {
        log.info("Processando notificacao por EMAIL para paciente ID: {}", notificacao.getPacienteId());
        emailService.enviarEmail(notificacao.getPacienteId(), notificacao.getMensagem());
    }
    
    /**
     * Processa notificacao por SMS (apenas log)
     */
    private void processarSms(NotificacaoDTO notificacao) {
        log.info("Processando notificacao por SMS para paciente ID: {}", notificacao.getPacienteId());
        smsService.enviarSms(notificacao.getPacienteId(), notificacao.getMensagem());
    }
    
    /**
     * Verifica se o servico esta configurado corretamente
     */
    public boolean isConfigurado() {
        boolean configOk = true;
        
        if (properties.getModo() == NotificacaoProperties.ModoNotificacao.EMAIL && !emailService.isDisponivel()) {
            log.warn("Modo EMAIL configurado mas EmailService nao disponivel");
            configOk = false;
        }
        
        log.info("Servico de notificacao configurado - Modo: {}", properties.getModo());
        
        return configOk;
    }
}

package com.challenge.notificacao.service;

import com.challenge.notificacao.config.NotificacaoProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private NotificacaoProperties properties;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    public void enviarEmail(Long pacienteId, String mensagem) {
        if (!properties.getEmail().isHabilitado()) {
            log.debug("Email desabilitado, pulando envio");
            return;
        }
        
        if (mailSender == null) {
            log.warn("JavaMailSender nao configurado. Verifique as configuracoes SMTP.");
            logEmailSimulado(pacienteId, mensagem);
            return;
        }
        
        try {
            // Em producao, aqui voce buscaria o email real do paciente
            String emailPaciente = buscarEmailPaciente(pacienteId);
            
            if (emailPaciente == null || emailPaciente.isEmpty()) {
                log.warn("Email do paciente ID {} nao encontrado", pacienteId);
                logEmailSimulado(pacienteId, mensagem);
                return;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailPaciente);
            message.setSubject(properties.getEmail().getAssuntoPadrao());
            message.setText(construirCorpoEmail(mensagem, pacienteId));
            
            mailSender.send(message);
            log.info("Email enviado com sucesso para paciente ID: {} - Email: {}", 
                    pacienteId, emailPaciente);
            
        } catch (Exception e) {
            log.error("Erro ao enviar email para paciente ID {}: {}", 
                    pacienteId, e.getMessage());
            logEmailSimulado(pacienteId, mensagem);
        }
    }
    
    private String buscarEmailPaciente(Long pacienteId) {
        return "paciente" + pacienteId + "@exemplo.com";
    }
    
    private String construirCorpoEmail(String mensagem, Long pacienteId) {
        StringBuilder corpo = new StringBuilder();
        corpo.append("Ola,\n\n");
        corpo.append("Voce tem uma nova notificacao sobre sua consulta medica:\n\n");
        corpo.append(mensagem).append("\n\n");
        corpo.append("---\n");
        corpo.append("Este e um email automatico do sistema de agendamento medico.\n");
        corpo.append("Paciente ID: ").append(pacienteId).append("\n");
        corpo.append("Data/Hora: ").append(java.time.LocalDateTime.now()).append("\n");
        
        return corpo.toString();
    }
    
    private void logEmailSimulado(Long pacienteId, String mensagem) {
        log.info("SIMULACAO: Email enviado para paciente ID {}", pacienteId);
        log.info("SIMULACAO: Assunto: {}", properties.getEmail().getAssuntoPadrao());
        log.info("SIMULACAO: Mensagem: {}", mensagem);
        log.info("SIMULACAO: Para configurar email real, defina as variaveis SMTP_USERNAME e SMTP_PASSWORD");
    }
    
    public boolean isDisponivel() {
        return mailSender != null && properties.getEmail().isHabilitado();
    }
    
    public void testarConfiguracao() {
        log.info("Testando configuracao de email...");
        
        if (mailSender == null) {
            log.warn("JavaMailSender nao configurado");
            return;
        }
        
        if (!properties.getEmail().isHabilitado()) {
            log.warn("Email desabilitado nas configuracoes");
            return;
        }
        
        log.info("JavaMailSender configurado");
        log.info("Email habilitado");
        log.info("Assunto padrao: {}", properties.getEmail().getAssuntoPadrao());
    }
}

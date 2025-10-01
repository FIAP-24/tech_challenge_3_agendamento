package com.challenge.notificacao.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configurações do serviço de notificação
 */
@Component
@ConfigurationProperties(prefix = "notificacao")
public class NotificacaoProperties {
    
    /**
     * Modo de notificação: LOG, EMAIL, SMS
     */
    private ModoNotificacao modo = ModoNotificacao.LOG;
    
    /**
     * Configurações de email
     */
    private Email email = new Email();
    
    /**
     * Configurações de SMS
     */
    private Sms sms = new Sms();
    
    public enum ModoNotificacao {
        LOG,    // Apenas log
        EMAIL,  // Envio por email
        SMS,    // Envio por SMS
        AMBOS   // Email e SMS
    }
    
    public static class Email {
        private boolean habilitado = true;
        private String assuntoPadrao = "Notificação de Consulta Médica";
        private String templatePadrao = "Olá, você tem uma nova notificação sobre sua consulta médica.";
        
        // Getters e Setters
        public boolean isHabilitado() { return habilitado; }
        public void setHabilitado(boolean habilitado) { this.habilitado = habilitado; }
        
        public String getAssuntoPadrao() { return assuntoPadrao; }
        public void setAssuntoPadrao(String assuntoPadrao) { this.assuntoPadrao = assuntoPadrao; }
        
        public String getTemplatePadrao() { return templatePadrao; }
        public void setTemplatePadrao(String templatePadrao) { this.templatePadrao = templatePadrao; }
    }
    
    public static class Sms {
        private boolean habilitado = false;
        private String provedor = "TWILIO"; // TWILIO, AWS_SNS, etc.
        private String templatePadrao = "Lembrete: Você tem uma consulta médica agendada.";
        
        // Getters e Setters
        public boolean isHabilitado() { return habilitado; }
        public void setHabilitado(boolean habilitado) { this.habilitado = habilitado; }
        
        public String getProvedor() { return provedor; }
        public void setProvedor(String provedor) { this.provedor = provedor; }
        
        public String getTemplatePadrao() { return templatePadrao; }
        public void setTemplatePadrao(String templatePadrao) { this.templatePadrao = templatePadrao; }
    }
    
    // Getters e Setters principais
    public ModoNotificacao getModo() { return modo; }
    public void setModo(ModoNotificacao modo) { this.modo = modo; }
    
    public Email getEmail() { return email; }
    public void setEmail(Email email) { this.email = email; }
    
    public Sms getSms() { return sms; }
    public void setSms(Sms sms) { this.sms = sms; }
}

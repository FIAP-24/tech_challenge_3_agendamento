package com.challenge.notificacao.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.challenge.notificacao.config.NotificacaoProperties;

/**
 * Servico para envio de SMS
 * Atualmente implementado apenas com log (sem provedores reais)
 */
@Service
public class SmsService {
    
    private static final Logger log = LoggerFactory.getLogger(SmsService.class);
    
    @Autowired
    private NotificacaoProperties properties;
    
    /**
     * Envia SMS para um paciente (apenas log)
     */
    public void enviarSms(Long pacienteId, String mensagem) {
        log.info("=== ENVIO DE SMS (LOG) ===");
        log.info("Paciente ID: {}", pacienteId);
        log.info("Mensagem: {}", mensagem);
        log.info("Provedor configurado: {}", properties.getSms().getProvedor());
        log.info("===========================");
        
        simularEnvioSms(pacienteId, mensagem);
    }
    
    /**
     * Simula o envio de SMS (apenas log)
     */
    private void simularEnvioSms(Long pacienteId, String mensagem) {
        String provedor = properties.getSms().getProvedor();
        
        log.info("[SIMULACAO] Enviando SMS para paciente ID: {}", pacienteId);
        log.info("[SIMULACAO] Provedor: {}", provedor);
        log.info("[SIMULACAO] Mensagem: {}", mensagem);
        log.info("[SIMULACAO] Status: SIMULADO (nao enviado realmente)");
        log.info("[SIMULACAO] Para implementar SMS real, configure um provedor SMS");
    }
    
    /**
     * Verifica se o servico SMS esta disponivel
     * SMS sempre esta disponivel (apenas log)
     */
    public boolean isDisponivel() {
        return true; // SMS sempre disponivel (apenas log)
    }
}

package com.challenge.notificacao.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de dados de notificacao entre microservicos
 */
public class NotificacaoDTO {
    
    private final Long consultaId;
    private final Long pacienteId;
    private final String mensagem;
    private final LocalDateTime timestamp;
    private final TipoNotificacao tipo;
    
    @JsonCreator
    public NotificacaoDTO(
            @JsonProperty("consultaId") Long consultaId,
            @JsonProperty("pacienteId") Long pacienteId,
            @JsonProperty("mensagem") String mensagem) {
        this.consultaId = consultaId;
        this.pacienteId = pacienteId;
        this.mensagem = mensagem;
        this.timestamp = LocalDateTime.now();
        this.tipo = TipoNotificacao.CONSULTA;
    }
    
    public NotificacaoDTO(Long consultaId, Long pacienteId, String mensagem, TipoNotificacao tipo) {
        this.consultaId = consultaId;
        this.pacienteId = pacienteId;
        this.mensagem = mensagem;
        this.timestamp = LocalDateTime.now();
        this.tipo = tipo;
    }
    
    // Getters
    public Long getConsultaId() { return consultaId; }
    public Long getPacienteId() { return pacienteId; }
    public String getMensagem() { return mensagem; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public TipoNotificacao getTipo() { return tipo; }
    
    public enum TipoNotificacao {
        CONSULTA,
        LEMBRETE,
        CANCELAMENTO,
        REMARCACAO
    }
    
    @Override
    public String toString() {
        return String.format("NotificacaoDTO{consultaId=%d, pacienteId=%d, mensagem='%s', timestamp=%s, tipo=%s}", 
                consultaId, pacienteId, mensagem, timestamp, tipo);
    }
}


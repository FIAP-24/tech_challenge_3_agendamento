package com.challenge.historico.dto;

import java.io.Serializable;

public class NotificacaoDTO implements Serializable {
    private Long consultaId;
    private Long pacienteId;
    private String mensagem;

    // Getters e Setters
    public Long getConsultaId() { return consultaId; }
    public void setConsultaId(Long consultaId) { this.consultaId = consultaId; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}
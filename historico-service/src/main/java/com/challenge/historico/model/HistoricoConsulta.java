package com.challenge.historico.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class HistoricoConsulta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long consultaId;
    private Long pacienteId;
    private String evento;
    private LocalDateTime timestamp;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getConsultaId() { return consultaId; }
    public void setConsultaId(Long consultaId) { this.consultaId = consultaId; }
    public Long getPacienteId() { return pacienteId; }
    public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }
    public String getEvento() { return evento; }
    public void setEvento(String evento) { this.evento = evento; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
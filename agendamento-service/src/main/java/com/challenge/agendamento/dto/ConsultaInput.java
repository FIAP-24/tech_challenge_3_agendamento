package com.challenge.agendamento.dto;

public record ConsultaInput(String pacienteId, String medicoId, String dataHora, String descricao) {
}

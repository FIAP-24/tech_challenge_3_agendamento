package com.challenge.agendamento.dto;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificacaoDTO implements Serializable {
    private Long consultaId;
    private Long pacienteId;
    private String mensagem;
}
package com.challenge.agendamento.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record MedicoInput(
        @NotBlank(message = "Nome é obrigatório")
        String nome,
        
        @NotBlank(message = "CRM é obrigatório")
        @Pattern(regexp = "\\d{4,6}", message = "CRM deve ter entre 4 e 6 dígitos")
        String crm,
        
        @NotBlank(message = "Especialidade é obrigatória")
        String especialidade,
        
        @Email(message = "Email deve ser válido")
        String email,
        
        @Pattern(regexp = "\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}", message = "Telefone deve estar no formato (11) 99999-9999")
        String telefone,
        
        Boolean ativo
) {
}

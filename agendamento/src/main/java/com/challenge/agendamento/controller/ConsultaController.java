package com.challenge.agendamento.controller;

import com.challenge.agendamento.dto.ConsultaInput;
import com.challenge.agendamento.model.Consulta;
import com.challenge.agendamento.service.ConsultaService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService consultaService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Métodos de Query (leitura)
    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')") // Acesso controlado [cite: 16, 17, 18]
    public List<Consulta> consultasPorPaciente(@Argument Long pacienteId) {
        // Adicionar lógica de autorização para garantir que um paciente só veja suas consultas
        return consultaService.findConsultasByPacienteId(pacienteId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public List<Consulta> proximasConsultas(@Argument Long pacienteId) {
        return consultaService.findProximasConsultasByPacienteId(pacienteId);
    }

    // Métodos de Mutation (escrita)
    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')") // Apenas médicos e enfermeiros podem registrar [cite: 21]
    public Consulta registrarConsulta(@Argument ConsultaInput input) {
        Consulta consulta = new Consulta();
        consulta.setPacienteId(Long.valueOf(input.pacienteId()));
        consulta.setMedicoId(Long.valueOf(input.medicoId()));
        consulta.setDataHora(LocalDateTime.parse(input.dataHora(), formatter));
        consulta.setDescricao(input.descricao());
        return consultaService.registrarConsulta(consulta);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')") // Apenas médicos e enfermeiros podem editar [cite: 21]
    public Consulta editarConsulta(@Argument Long id, @Argument ConsultaInput input) {
        Consulta consultaAtualizada = new Consulta();
        consultaAtualizada.setDataHora(LocalDateTime.parse(input.dataHora(), formatter));
        consultaAtualizada.setDescricao(input.descricao());
        return consultaService.editarConsulta(id, consultaAtualizada)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
    }


}
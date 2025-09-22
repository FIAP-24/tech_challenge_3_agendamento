package com.challenge.agendamento.controller;

import com.challenge.agendamento.dto.ConsultaInput;
import com.challenge.agendamento.model.Consulta;
import com.challenge.agendamento.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ConsultaController {

    private static final Logger log = LoggerFactory.getLogger(ConsultaController.class);

    @Autowired
    private ConsultaService consultaService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    // Métodos de Query (leitura)
    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')") // Acesso controlado [cite: 16, 17, 18]
    public List<Consulta> consultasPorPaciente(@Argument Long pacienteId) {
        log.info("Buscando consultas para paciente ID: {}", pacienteId);
        // Adicionar lógica de autorização para garantir que um paciente só veja suas consultas
        return consultaService.findConsultasByPacienteId(pacienteId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public List<Consulta> proximasConsultas(@Argument Long pacienteId) {
        log.info("Buscando próximas consultas para paciente ID: {}", pacienteId);
        return consultaService.findProximasConsultasByPacienteId(pacienteId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public Consulta consultaPorId(@Argument Long id) {
        log.info("Buscando consulta por ID: {}", id);
        return consultaService.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
    }

    // Métodos de Mutation (escrita)
    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')") // Apenas médicos e enfermeiros podem registrar [cite: 21]
    public Consulta registrarConsulta(@Argument ConsultaInput input) {
        log.info("Registrando nova consulta para paciente ID: {}", input.pacienteId());
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
        log.info("Editando consulta ID: {}", id);
        Consulta consultaAtualizada = new Consulta();
        consultaAtualizada.setDataHora(LocalDateTime.parse(input.dataHora(), formatter));
        consultaAtualizada.setDescricao(input.descricao());
        return consultaService.editarConsulta(id, consultaAtualizada)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
    }


}
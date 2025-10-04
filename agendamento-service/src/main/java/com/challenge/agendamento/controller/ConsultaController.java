package com.challenge.agendamento.controller;

import com.challenge.agendamento.dto.ConsultaInput;
import com.challenge.agendamento.model.Consulta;
import com.challenge.agendamento.service.ConsultaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public List<Consulta> consultasPorPaciente(@Argument Long pacienteId) {
        log.info("Buscando consultas para paciente ID: {}", pacienteId);

        verificarAutorizacaoPaciente(pacienteId);

        return consultaService.findConsultasByPacienteId(pacienteId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public List<Consulta> proximasConsultas(@Argument Long pacienteId) {
        log.info("Buscando próximas consultas para paciente ID: {}", pacienteId);

        verificarAutorizacaoPaciente(pacienteId);

        return consultaService.findProximasConsultasByPacienteId(pacienteId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public Consulta consultaPorId(@Argument Long id) {
        log.info("Buscando consulta por ID: {}", id);
        Consulta consulta = consultaService.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        verificarAutorizacaoPaciente(consulta.getPacienteId());

        return consulta;
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public List<Consulta> consultasPorMedico(@Argument Long medicoId) {
        log.info("Buscando consultas para médico ID: {}", medicoId);
        return consultaService.findConsultasByMedicoId(medicoId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public List<Consulta> proximasConsultasMedico(@Argument Long medicoId) {
        log.info("Buscando próximas consultas para médico ID: {}", medicoId);
        return consultaService.findProximasConsultasByMedicoId(medicoId);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public List<Consulta> consultasMedicoPorPeriodo(@Argument Long medicoId, @Argument String dataInicio, @Argument String dataFim) {
        log.info("Buscando consultas do médico ID: {} entre {} e {}", medicoId, dataInicio, dataFim);
        
        LocalDateTime inicio = LocalDateTime.parse(dataInicio, formatter);
        LocalDateTime fim = LocalDateTime.parse(dataFim, formatter);
        
        return consultaService.findConsultasByMedicoIdAndPeriodo(medicoId, inicio, fim);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
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

    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public Boolean cancelarConsulta(@Argument Long id) {
        log.info("Cancelando consulta ID: {}", id);
        return consultaService.cancelarConsulta(id);
    }

    /**
     * Verifica se o usuário autenticado tem permissão para acessar os dados do paciente.
     * Se o usuário for um paciente, ele só pode acessar seus próprios dados.
     *
     * @param pacienteId O ID do paciente a ser acessado.
     * @throws AccessDeniedException se o acesso for negado.
     */
    private void verificarAutorizacaoPaciente(Long pacienteId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isPaciente = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_PACIENTE"));

        if (isPaciente) {

            Long authenticatedPacienteId;
            try {
                authenticatedPacienteId = Long.valueOf(authentication.getName());
            } catch (NumberFormatException e) {
                throw new AccessDeniedException("Acesso negado: ID de paciente inválido.");
            }

            if (!authenticatedPacienteId.equals(pacienteId)) {
                throw new AccessDeniedException("Acesso negado: Pacientes só podem acessar seus próprios dados.");
            }
        }
    }
}
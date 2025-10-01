package com.challenge.agendamento.controller;

import com.challenge.agendamento.dto.PacienteInput;
import com.challenge.agendamento.model.Paciente;
import com.challenge.agendamento.service.PacienteService;
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

import java.util.List;

@Controller
public class PacienteController {

    private static final Logger log = LoggerFactory.getLogger(PacienteController.class);

    @Autowired
    private PacienteService pacienteService;

    /**
     * Lista todos os pacientes
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public List<Paciente> pacientes() {
        log.info("Listando todos os pacientes");
        return pacienteService.listarTodosPacientes();
    }

    /**
     * Lista pacientes ativos
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public List<Paciente> pacientesAtivos() {
        log.info("Listando pacientes ativos");
        return pacienteService.listarPacientesAtivos();
    }

    /**
     * Busca paciente por ID, com verificação de autorização.
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public Paciente pacientePorId(@Argument Long id) {
        log.info("Buscando paciente por ID: {}", id);

        verificarAutorizacaoPaciente(id);

        return pacienteService.buscarPacientePorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + id));
    }

    /**
     * Busca paciente por CPF
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public Paciente pacientePorCpf(@Argument String cpf) {
        log.info("Buscando paciente por CPF: {}", cpf);
        return pacienteService.buscarPacientePorCpf(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado com CPF: " + cpf));
    }

    /**
     * Busca pacientes por nome
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public List<Paciente> pacientesPorNome(@Argument String nome) {
        log.info("Buscando pacientes por nome: {}", nome);
        return pacienteService.buscarPacientesPorNome(nome);
    }

    /**
     * Busca pacientes por cidade
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public List<Paciente> pacientesPorCidade(@Argument String cidade) {
        log.info("Buscando pacientes por cidade: {}", cidade);
        return pacienteService.buscarPacientesPorCidade(cidade);
    }

    /**
     * Cria um novo paciente
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public Paciente criarPaciente(@Argument PacienteInput input) {
        log.info("Criando novo paciente: {}", input.getNome());
        return pacienteService.criarPaciente(input);
    }

    /**
     * Edita um paciente existente
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public Paciente editarPaciente(@Argument Long id, @Argument PacienteInput input) {
        log.info("Editando paciente ID: {}", id);
        return pacienteService.editarPaciente(id, input);
    }

    /**
     * Desativa um paciente (soft delete)
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public Boolean desativarPaciente(@Argument Long id) {
        log.info("Desativando paciente ID: {}", id);
        pacienteService.desativarPaciente(id);
        return true;
    }

    /**
     * Ativa um paciente
     */
    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public Boolean ativarPaciente(@Argument Long id) {
        log.info("Ativando paciente ID: {}", id);
        pacienteService.ativarPaciente(id);
        return true;
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
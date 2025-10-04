package com.challenge.historico.controller;

import com.challenge.historico.model.HistoricoConsulta;
import com.challenge.historico.service.HistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Controller GraphQL para o serviço de histórico.
 * Implementa controle de acesso baseado em roles:
 * - Médicos e enfermeiros podem visualizar histórico de qualquer paciente
 * - Pacientes podem visualizar apenas seu próprio histórico
 */
@Controller
public class HistoricoController {

    private static final Logger log = LoggerFactory.getLogger(HistoricoController.class);

    @Autowired
    private HistoricoService historicoService;

    /**
     * Busca o histórico de consultas de um paciente.
     * Médicos e enfermeiros podem visualizar qualquer histórico.
     * Pacientes podem visualizar apenas seu próprio histórico.
     */
    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO', 'PACIENTE')")
    public List<HistoricoConsulta> historicoPorPaciente(@Argument Long pacienteId) {
        log.info("Buscando histórico para paciente ID: {}", pacienteId);
        
        // Verifica se o usuário tem permissão para acessar este histórico
        verificarAutorizacaoPaciente(pacienteId);
        
        return historicoService.buscarHistoricoPorPaciente(pacienteId);
    }

    /**
     * Verifica se o usuário autenticado tem permissão para acessar o histórico do paciente.
     * Médicos e enfermeiros têm acesso a todos os históricos.
     * Pacientes só podem acessar seu próprio histórico.
     *
     * @param pacienteId O ID do paciente cujo histórico está sendo acessado
     * @throws AccessDeniedException se o acesso for negado
     */
    private void verificarAutorizacaoPaciente(Long pacienteId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Verifica se o usuário é um paciente
        boolean isPaciente = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_PACIENTE"));

        if (isPaciente) {
            // Se for paciente, verifica se está tentando acessar seu próprio histórico
            Long authenticatedPacienteId;
            try {
                authenticatedPacienteId = Long.valueOf(authentication.getName());
            } catch (NumberFormatException e) {
                throw new AccessDeniedException("Acesso negado: ID de paciente inválido.");
            }

            if (!authenticatedPacienteId.equals(pacienteId)) {
                throw new AccessDeniedException("Acesso negado: Pacientes só podem acessar seu próprio histórico.");
            }
        }
        // Médicos e enfermeiros podem acessar qualquer histórico
        log.info("Acesso autorizado para usuário: {}", authentication.getName());
    }
}
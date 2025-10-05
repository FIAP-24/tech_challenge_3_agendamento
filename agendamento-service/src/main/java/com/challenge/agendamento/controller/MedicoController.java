package com.challenge.agendamento.controller;

import com.challenge.agendamento.dto.MedicoInput;
import com.challenge.agendamento.model.Medico;
import com.challenge.agendamento.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
public class MedicoController {

    private static final Logger log = LoggerFactory.getLogger(MedicoController.class);

    @Autowired
    private MedicoService medicoService;

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public List<Medico> medicos() {
        log.info("Listando todos os médicos");
        return medicoService.listarTodosMedicos();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public List<Medico> medicosAtivos() {
        log.info("Listando médicos ativos");
        return medicoService.listarMedicosAtivos();
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public Medico medicoPorId(@Argument Long id) {
        log.info("Buscando médico por ID: {}", id);
        return medicoService.buscarMedicoPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado: " + id));
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public Medico medicoPorCrm(@Argument String crm) {
        log.info("Buscando médico por CRM: {}", crm);
        return medicoService.buscarMedicoPorCrm(crm)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado com CRM: " + crm));
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public List<Medico> medicosPorEspecialidade(@Argument String especialidade) {
        log.info("Buscando médicos por especialidade: {}", especialidade);
        return medicoService.buscarMedicosPorEspecialidade(especialidade);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public List<Medico> medicosAtivosPorEspecialidade(@Argument String especialidade) {
        log.info("Buscando médicos ativos por especialidade: {}", especialidade);
        return medicoService.buscarMedicosAtivosPorEspecialidade(especialidade);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public List<Medico> medicosPorNome(@Argument String nome) {
        log.info("Buscando médicos por nome: {}", nome);
        return medicoService.buscarMedicosPorNome(nome);
    }

    @QueryMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public List<Medico> medicosAtivosPorNome(@Argument String nome) {
        log.info("Buscando médicos ativos por nome: {}", nome);
        return medicoService.buscarMedicosAtivosPorNome(nome);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public Medico criarMedico(@Argument MedicoInput input) {
        log.info("Criando novo médico: {}", input.nome());
        return medicoService.criarMedico(input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public Medico editarMedico(@Argument Long id, @Argument MedicoInput input) {
        log.info("Editando médico ID: {}", id);
        return medicoService.editarMedico(id, input);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public Boolean desativarMedico(@Argument Long id) {
        log.info("Desativando médico ID: {}", id);
        return medicoService.desativarMedico(id);
    }

    @MutationMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    public Boolean ativarMedico(@Argument Long id) {
        log.info("Ativando médico ID: {}", id);
        return medicoService.ativarMedico(id);
    }
}

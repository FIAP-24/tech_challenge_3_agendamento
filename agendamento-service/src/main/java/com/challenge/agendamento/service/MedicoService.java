package com.challenge.agendamento.service;

import com.challenge.agendamento.dto.MedicoInput;
import com.challenge.agendamento.model.Medico;
import com.challenge.agendamento.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {

    private static final Logger log = LoggerFactory.getLogger(MedicoService.class);

    @Autowired
    private MedicoRepository medicoRepository;

    @Transactional
    public Medico criarMedico(MedicoInput input) {
        log.info("Criando novo médico: {}", input.nome());

        if (medicoRepository.existsByCrm(input.crm())) {
            throw new IllegalArgumentException("CRM já cadastrado: " + input.crm());
        }

        if (input.email() != null && !input.email().isEmpty() && 
            medicoRepository.existsByEmail(input.email())) {
            throw new IllegalArgumentException("Email já cadastrado: " + input.email());
        }

        Medico medico = new Medico();
        medico.setNome(input.nome());
        medico.setCrm(input.crm());
        medico.setEspecialidade(input.especialidade());
        medico.setEmail(input.email());
        medico.setTelefone(input.telefone());
        medico.setAtivo(input.ativo() != null ? input.ativo() : true);

        Medico savedMedico = medicoRepository.save(medico);
        log.info("Médico criado com sucesso. ID: {}, CRM: {}", savedMedico.getId(), savedMedico.getCrm());
        
        return savedMedico;
    }

    @Transactional
    public Medico editarMedico(Long id, MedicoInput input) {
        log.info("Editando médico ID: {}", id);
        
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado: " + id));

        if (medicoRepository.existsByCrm(input.crm()) && !medico.getCrm().equals(input.crm())) {
            throw new IllegalArgumentException("CRM já cadastrado: " + input.crm());
        }

        if (input.email() != null && !input.email().isEmpty() && 
            medicoRepository.existsByEmail(input.email()) && !input.email().equals(medico.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado: " + input.email());
        }

        medico.setNome(input.nome());
        medico.setCrm(input.crm());
        medico.setEspecialidade(input.especialidade());
        medico.setEmail(input.email());
        medico.setTelefone(input.telefone());
        if (input.ativo() != null) {
            medico.setAtivo(input.ativo());
        }

        Medico savedMedico = medicoRepository.save(medico);
        log.info("Médico editado com sucesso. ID: {}, CRM: {}", savedMedico.getId(), savedMedico.getCrm());
        
        return savedMedico;
    }

    @Transactional
    public Boolean desativarMedico(Long id) {
        log.info("Desativando médico ID: {}", id);
        
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado: " + id));

        medico.setAtivo(false);
        medicoRepository.save(medico);
        
        log.info("Médico desativado com sucesso. ID: {}", id);
        return true;
    }

    @Transactional
    public Boolean ativarMedico(Long id) {
        log.info("Ativando médico ID: {}", id);
        
        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médico não encontrado: " + id));

        medico.setAtivo(true);
        medicoRepository.save(medico);
        
        log.info("Médico ativado com sucesso. ID: {}", id);
        return true;
    }

    public List<Medico> listarTodosMedicos() {
        log.info("Listando todos os médicos");
        return medicoRepository.findAll();
    }

    public List<Medico> listarMedicosAtivos() {
        log.info("Listando médicos ativos");
        return medicoRepository.findByAtivoTrue();
    }

    public Optional<Medico> buscarMedicoPorId(Long id) {
        log.info("Buscando médico por ID: {}", id);
        return medicoRepository.findById(id);
    }

    public Optional<Medico> buscarMedicoPorCrm(String crm) {
        log.info("Buscando médico por CRM: {}", crm);
        return medicoRepository.findByCrm(crm);
    }

    public List<Medico> buscarMedicosPorEspecialidade(String especialidade) {
        log.info("Buscando médicos por especialidade: {}", especialidade);
        return medicoRepository.findByEspecialidade(especialidade);
    }

    public List<Medico> buscarMedicosAtivosPorEspecialidade(String especialidade) {
        log.info("Buscando médicos ativos por especialidade: {}", especialidade);
        return medicoRepository.findByAtivoTrueAndEspecialidade(especialidade);
    }

    public List<Medico> buscarMedicosPorNome(String nome) {
        log.info("Buscando médicos por nome: {}", nome);
        return medicoRepository.findByNomeContaining(nome);
    }

    public List<Medico> buscarMedicosAtivosPorNome(String nome) {
        log.info("Buscando médicos ativos por nome: {}", nome);
        return medicoRepository.findByAtivoTrueAndNomeContaining(nome);
    }

    public boolean medicoExisteEAtivo(Long id) {
        Optional<Medico> medico = medicoRepository.findById(id);
        return medico.isPresent() && medico.get().getAtivo();
    }

    public boolean medicoExistePorCrm(String crm) {
        return medicoRepository.existsByCrm(crm);
    }
}

package com.challenge.agendamento.service;

import com.challenge.agendamento.dto.PacienteInput;
import com.challenge.agendamento.model.Paciente;
import com.challenge.agendamento.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    private static final Logger log = LoggerFactory.getLogger(PacienteService.class);

    @Autowired
    private PacienteRepository pacienteRepository;

    /**
     * Cria um novo paciente
     */
    @Transactional
    public Paciente criarPaciente(PacienteInput input) {
        log.info("Criando novo paciente: {}", input.getNome());

        // Validar CPF único
        if (pacienteRepository.findByCpf(input.getCpf()).isPresent()) {
            throw new IllegalArgumentException("CPF já cadastrado: " + input.getCpf());
        }

        // Validar email único (se fornecido)
        if (input.getEmail() != null && !input.getEmail().isEmpty()) {
            if (pacienteRepository.findByEmail(input.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email já cadastrado: " + input.getEmail());
            }
        }

        Paciente paciente = new Paciente();
        paciente.setNome(input.getNome());
        paciente.setCpf(input.getCpf());
        paciente.setEmail(input.getEmail());
        paciente.setTelefone(input.getTelefone());
        paciente.setDataNascimento(input.getDataNascimento());
        paciente.setEndereco(input.getEndereco());
        paciente.setCidade(input.getCidade());
        paciente.setEstado(input.getEstado());
        paciente.setCep(input.getCep());
        paciente.setAtivo(input.getAtivo() != null ? input.getAtivo() : true);

        Paciente savedPaciente = pacienteRepository.save(paciente);
        log.info("Paciente criado com sucesso. ID: {}", savedPaciente.getId());

        return savedPaciente;
    }

    /**
     * Edita um paciente existente
     */
    @Transactional
    public Paciente editarPaciente(Long id, PacienteInput input) {
        log.info("Editando paciente ID: {}", id);

        return pacienteRepository.findById(id).map(pacienteExistente -> {
            // Validar CPF único (excluindo o próprio paciente)
            if (input.getCpf() != null && !input.getCpf().equals(pacienteExistente.getCpf())) {
                if (pacienteRepository.findByCpf(input.getCpf()).isPresent()) {
                    throw new IllegalArgumentException("CPF já cadastrado: " + input.getCpf());
                }
                pacienteExistente.setCpf(input.getCpf());
            }

            // Validar email único (excluindo o próprio paciente)
            if (input.getEmail() != null && !input.getEmail().equals(pacienteExistente.getEmail())) {
                if (pacienteRepository.findByEmail(input.getEmail()).isPresent()) {
                    throw new IllegalArgumentException("Email já cadastrado: " + input.getEmail());
                }
                pacienteExistente.setEmail(input.getEmail());
            }

            // Atualizar outros campos
            if (input.getNome() != null) pacienteExistente.setNome(input.getNome());
            if (input.getTelefone() != null) pacienteExistente.setTelefone(input.getTelefone());
            if (input.getDataNascimento() != null) pacienteExistente.setDataNascimento(input.getDataNascimento());
            if (input.getEndereco() != null) pacienteExistente.setEndereco(input.getEndereco());
            if (input.getCidade() != null) pacienteExistente.setCidade(input.getCidade());
            if (input.getEstado() != null) pacienteExistente.setEstado(input.getEstado());
            if (input.getCep() != null) pacienteExistente.setCep(input.getCep());
            if (input.getAtivo() != null) pacienteExistente.setAtivo(input.getAtivo());

            pacienteExistente.setDataAtualizacao(LocalDateTime.now());

            Paciente updatedPaciente = pacienteRepository.save(pacienteExistente);
            log.info("Paciente ID: {} editado com sucesso.", updatedPaciente.getId());

            return updatedPaciente;
        }).orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado: " + id));
    }

    /**
     * Desativa um paciente (soft delete)
     */
    @Transactional
    public void desativarPaciente(Long id) {
        log.info("Desativando paciente ID: {}", id);

        pacienteRepository.findById(id).ifPresentOrElse(paciente -> {
            paciente.setAtivo(false);
            paciente.setDataAtualizacao(LocalDateTime.now());
            pacienteRepository.save(paciente);
            log.info("Paciente ID: {} desativado com sucesso.", id);
        }, () -> {
            throw new IllegalArgumentException("Paciente não encontrado: " + id);
        });
    }

    /**
     * Ativa um paciente
     */
    @Transactional
    public void ativarPaciente(Long id) {
        log.info("Ativando paciente ID: {}", id);

        pacienteRepository.findById(id).ifPresentOrElse(paciente -> {
            paciente.setAtivo(true);
            paciente.setDataAtualizacao(LocalDateTime.now());
            pacienteRepository.save(paciente);
            log.info("Paciente ID: {} ativado com sucesso.", id);
        }, () -> {
            throw new IllegalArgumentException("Paciente não encontrado: " + id);
        });
    }

    /**
     * Busca paciente por ID
     */
    public Optional<Paciente> buscarPacientePorId(Long id) {
        return pacienteRepository.findById(id);
    }

    /**
     * Busca paciente por CPF
     */
    public Optional<Paciente> buscarPacientePorCpf(String cpf) {
        return pacienteRepository.findByCpf(cpf);
    }

    /**
     * Lista todos os pacientes
     */
    public List<Paciente> listarTodosPacientes() {
        return pacienteRepository.findAll();
    }

    /**
     * Lista apenas pacientes ativos
     */
    public List<Paciente> listarPacientesAtivos() {
        return pacienteRepository.findByAtivoTrue();
    }

    /**
     * Busca pacientes por nome
     */
    public List<Paciente> buscarPacientesPorNome(String nome) {
        return pacienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Busca pacientes por cidade
     */
    public List<Paciente> buscarPacientesPorCidade(String cidade) {
        return pacienteRepository.findByCidadeIgnoreCase(cidade);
    }

    /**
     * Verifica se paciente existe e está ativo
     */
    public boolean pacienteExisteEAtivo(Long pacienteId) {
        return buscarPacientePorId(pacienteId).map(Paciente::getAtivo).orElse(false);
    }
}
package com.challenge.agendamento.config;

import com.challenge.agendamento.model.Medico;
import com.challenge.agendamento.model.Paciente;
import com.challenge.agendamento.model.Usuario;
import com.challenge.agendamento.repository.MedicoRepository;
import com.challenge.agendamento.repository.PacienteRepository;
import com.challenge.agendamento.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PacienteRepository pacienteRepository;
    
    @Autowired
    private MedicoRepository medicoRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.count() == 0) {
            Usuario medico = new Usuario();
            medico.setUsername("medico1");
            medico.setPassword(passwordEncoder.encode("password123"));
            medico.setRoles(Set.of("MEDICO"));
            usuarioRepository.save(medico);

            Usuario enfermeiro = new Usuario();
            enfermeiro.setUsername("enfermeiro1");
            enfermeiro.setPassword(passwordEncoder.encode("password123"));
            enfermeiro.setRoles(Set.of("ENFERMEIRO"));
            usuarioRepository.save(enfermeiro);

            Usuario paciente = new Usuario();
            paciente.setUsername("1");
            paciente.setPassword(passwordEncoder.encode("password123"));
            paciente.setRoles(Set.of("PACIENTE"));
            usuarioRepository.save(paciente);

            System.out.println("Usuários de teste criados com sucesso!");
        }

        if (pacienteRepository.count() == 0) {
            try {
                Paciente pacienteTeste = new Paciente();
                pacienteTeste.setNome("Paciente Teste");
                pacienteTeste.setCpf("11122233344");
                pacienteTeste.setDataNascimento(LocalDate.of(1980, 1, 1));
                pacienteTeste.setAtivo(true);
                pacienteTeste.setDataCriacao(LocalDateTime.now());
                pacienteRepository.save(pacienteTeste);

                System.out.println("Paciente de teste criado com sucesso!");
            } catch (Exception e) {
                System.out.println("Paciente de teste já existe ou erro ao criar: " + e.getMessage());
            }
        }

        if (medicoRepository.count() == 0) {
            try {
                Medico medicoTeste = new Medico();
                medicoTeste.setNome("Dr. João Silva");
                medicoTeste.setCrm("12345");
                medicoTeste.setEspecialidade("Cardiologia");
                medicoTeste.setEmail("joao.silva@clinica.com");
                medicoTeste.setTelefone("(11) 99999-9999");
                medicoTeste.setAtivo(true);
                medicoTeste.setDataCriacao(LocalDateTime.now());
                medicoRepository.save(medicoTeste);

                Medico medicoTeste2 = new Medico();
                medicoTeste2.setNome("Dra. Maria Santos");
                medicoTeste2.setCrm("67890");
                medicoTeste2.setEspecialidade("Pediatria");
                medicoTeste2.setEmail("maria.santos@clinica.com");
                medicoTeste2.setTelefone("(11) 88888-8888");
                medicoTeste2.setAtivo(true);
                medicoTeste2.setDataCriacao(LocalDateTime.now());
                medicoRepository.save(medicoTeste2);

                System.out.println("Médicos de teste criados com sucesso!");
            } catch (Exception e) {
                System.out.println("Médicos de teste já existem ou erro ao criar: " + e.getMessage());
            }
        }
    }
}

package com.challenge.agendamento.config;

import com.challenge.agendamento.model.Paciente;
import com.challenge.agendamento.model.Usuario;
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
    }
}

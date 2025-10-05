package com.challenge.historico.config;

import com.challenge.historico.model.HistoricoConsulta;
import com.challenge.historico.repository.HistoricoConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private HistoricoConsultaRepository historicoRepository;

    @Override
    public void run(String... args) throws Exception {
        if (historicoRepository.count() == 0) {
            try {
                // Criar alguns registros de histórico de exemplo
                HistoricoConsulta historico1 = new HistoricoConsulta();
                historico1.setConsultaId(1L);
                historico1.setPacienteId(1L);
                historico1.setEvento("REGISTRO_CONSULTA - Consulta de exemplo criada");
                historico1.setTimestamp(LocalDateTime.now().minusHours(2));
                historicoRepository.save(historico1);

                HistoricoConsulta historico2 = new HistoricoConsulta();
                historico2.setConsultaId(2L);
                historico2.setPacienteId(1L);
                historico2.setEvento("EDICAO_CONSULTA - Consulta modificada");
                historico2.setTimestamp(LocalDateTime.now().minusHours(1));
                historicoRepository.save(historico2);

                HistoricoConsulta historico3 = new HistoricoConsulta();
                historico3.setConsultaId(3L);
                historico3.setPacienteId(2L);
                historico3.setEvento("CANCELAMENTO_CONSULTA - Consulta cancelada pelo paciente");
                historico3.setTimestamp(LocalDateTime.now().minusMinutes(30));
                historicoRepository.save(historico3);

                System.out.println("Históricos de teste criados com sucesso!");
            } catch (Exception e) {
                System.out.println("Históricos de teste já existem ou erro ao criar: " + e.getMessage());
            }
        }
    }
}

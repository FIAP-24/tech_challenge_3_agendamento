package com.challenge.historico.repository;

import com.challenge.historico.model.HistoricoConsulta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoricoConsultaRepository extends JpaRepository<HistoricoConsulta, Long> {
    List<HistoricoConsulta> findByPacienteIdOrderByTimestampDesc(Long pacienteId);
}
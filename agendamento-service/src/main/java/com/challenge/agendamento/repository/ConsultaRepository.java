package com.challenge.agendamento.repository;

import com.challenge.agendamento.model.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {
    List<Consulta> findByPacienteId(Long pacienteId);
    List<Consulta> findByPacienteIdAndDataHoraAfter(Long pacienteId, LocalDateTime dataHora);
    List<Consulta> findByMedicoId(Long medicoId);
    List<Consulta> findByMedicoIdAndDataHoraAfter(Long medicoId, LocalDateTime dataHora);
    List<Consulta> findByMedicoIdAndDataHoraBetween(Long medicoId, LocalDateTime inicio, LocalDateTime fim);
    boolean existsByMedicoIdAndDataHora(Long medicoId, LocalDateTime dataHora);
}
package com.challenge.historico.service;

import com.challenge.historico.model.HistoricoConsulta;
import com.challenge.historico.repository.HistoricoConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HistoricoService {

    @Autowired
    private HistoricoConsultaRepository repository;

    @Transactional
    public void salvarHistorico(HistoricoConsulta historico) {
        repository.save(historico);
    }

    public List<HistoricoConsulta> buscarHistoricoPorPaciente(Long pacienteId) {
        return repository.findByPacienteIdOrderByTimestampDesc(pacienteId);
    }
}
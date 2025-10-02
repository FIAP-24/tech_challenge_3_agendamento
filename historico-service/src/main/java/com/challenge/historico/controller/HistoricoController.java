package com.challenge.historico.controller;

import com.challenge.historico.model.HistoricoConsulta;
import com.challenge.historico.service.HistoricoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class HistoricoController {

    @Autowired
    private HistoricoService historicoService;

    @QueryMapping
    public List<HistoricoConsulta> historicoPorPaciente(@Argument Long pacienteId) {
        return historicoService.buscarHistoricoPorPaciente(pacienteId);
    }
}
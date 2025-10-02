package com.challenge.agendamento.service;

import com.challenge.agendamento.dto.NotificacaoDTO;
import com.challenge.agendamento.model.Consulta;
import com.challenge.agendamento.repository.ConsultaRepository;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultaService {

    private static final Logger log = LoggerFactory.getLogger(ConsultaService.class);

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired(required = false)
    private RabbitTemplate rabbitTemplate;

    @Autowired(required = false)
    private Queue queue;

    @Autowired
    private PacienteService pacienteService;

    @Transactional
    public Consulta registrarConsulta(Consulta consulta) {
        log.info("Registrando nova consulta para paciente ID: {}", consulta.getPacienteId());

        if (!pacienteService.pacienteExisteEAtivo(consulta.getPacienteId())) {
            throw new IllegalArgumentException("Paciente não encontrado ou inativo: " + consulta.getPacienteId());
        }

        if (consulta.getDataHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data da consulta deve ser futura");
        }

        Consulta savedConsulta = consultaRepository.save(consulta);
        enviarNotificacao("Consulta agendada com sucesso!", savedConsulta);
        enviarParaHistorico("REGISTRO_CONSULTA", savedConsulta);

        log.info("Consulta registrada com sucesso. ID: {}", savedConsulta.getId());
        return savedConsulta;
    }

    @Transactional
    public Optional<Consulta> editarConsulta(Long id, Consulta consultaAtualizada) {
        return consultaRepository.findById(id).map(consultaExistente -> {
            consultaExistente.setDataHora(consultaAtualizada.getDataHora());
            consultaExistente.setDescricao(consultaAtualizada.getDescricao());
            Consulta savedConsulta = consultaRepository.save(consultaExistente);
            enviarNotificacao("Sua consulta foi remarcada.", savedConsulta);
            enviarParaHistorico("EDICAO_CONSULTA", savedConsulta);
            return savedConsulta;
        });
    }

    public List<Consulta> findConsultasByPacienteId(Long pacienteId) {
        return consultaRepository.findByPacienteId(pacienteId);
    }

    public List<Consulta> findProximasConsultasByPacienteId(Long pacienteId) {
        return consultaRepository.findByPacienteIdAndDataHoraAfter(pacienteId, LocalDateTime.now());
    }

    public Optional<Consulta> findById(Long id) {
        return consultaRepository.findById(id);
    }

    private void enviarNotificacao(String mensagem, Consulta consulta) {
        if (rabbitTemplate != null && queue != null) {
            NotificacaoDTO notificacao = new NotificacaoDTO(
                    consulta.getId(),
                    consulta.getPacienteId(),
                    mensagem + " Para o dia: " + consulta.getDataHora().toString()
            );
            rabbitTemplate.convertAndSend(queue.getName(), notificacao);
            log.info("Notificação enviada via RabbitMQ para paciente ID: {}", consulta.getPacienteId());
        } else {
            log.warn("RabbitMQ não configurado - notificação não enviada para paciente ID: {}", consulta.getPacienteId());
        }
    }

    private void enviarParaHistorico(String acao, Consulta consulta) {
        if (rabbitTemplate != null) {
            String mensagem = String.format("Ação [%s] executada para a consulta. Descrição: %s", acao, consulta.getDescricao());
            NotificacaoDTO historicoDto = new NotificacaoDTO(
                    consulta.getId(),
                    consulta.getPacienteId(),
                    mensagem
            );
            rabbitTemplate.convertAndSend("historico.exchange", "historico.consulta.registrada", historicoDto);
            log.info("Mensagem de histórico enviada para consulta ID: {}", consulta.getId());
        } else {
            log.warn("RabbitMQ não configurado - histórico não enviado.");
        }
    }

}
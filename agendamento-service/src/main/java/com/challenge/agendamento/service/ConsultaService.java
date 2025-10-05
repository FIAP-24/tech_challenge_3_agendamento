package com.challenge.agendamento.service;

import com.challenge.agendamento.dto.NotificacaoDTO;
import com.challenge.agendamento.model.Consulta;
import com.challenge.agendamento.repository.ConsultaRepository;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private TopicExchange exchange;

    @Value("${rabbitmq.routing.key:notificacao.consulta}")
    private String routingKey;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private MedicoService medicoService;

    @Transactional
    public Consulta registrarConsulta(Consulta consulta) {
        log.info("Registrando nova consulta para paciente ID: {}", consulta.getPacienteId());

        if (!pacienteService.pacienteExisteEAtivo(consulta.getPacienteId())) {
            throw new IllegalArgumentException("Paciente não encontrado ou inativo: " + consulta.getPacienteId());
        }

        if (!medicoService.medicoExisteEAtivo(consulta.getMedicoId())) {
            throw new IllegalArgumentException("Médico não encontrado ou inativo: " + consulta.getMedicoId());
        }

        if (consulta.getDataHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data da consulta deve ser futura");
        }

        // Validar conflito de horário para o médico
        if (consultaRepository.existsByMedicoIdAndDataHora(consulta.getMedicoId(), consulta.getDataHora())) {
            throw new IllegalArgumentException("Médico já possui consulta agendada neste horário: " + consulta.getDataHora());
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

    public List<Consulta> findConsultasByMedicoId(Long medicoId) {
        return consultaRepository.findByMedicoId(medicoId);
    }

    public List<Consulta> findProximasConsultasByMedicoId(Long medicoId) {
        return consultaRepository.findByMedicoIdAndDataHoraAfter(medicoId, LocalDateTime.now());
    }

    public List<Consulta> findConsultasByMedicoIdAndPeriodo(Long medicoId, LocalDateTime inicio, LocalDateTime fim) {
        return consultaRepository.findByMedicoIdAndDataHoraBetween(medicoId, inicio, fim);
    }

    public Optional<Consulta> findById(Long id) {
        return consultaRepository.findById(id);
    }

    @Transactional
    public Boolean cancelarConsulta(Long id) {
        log.info("Cancelando consulta ID: {}", id);
        
        return consultaRepository.findById(id).map(consulta -> {
            if (consulta.getDataHora().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Não é possível cancelar uma consulta já realizada");
            }
            
            consultaRepository.delete(consulta);
            enviarParaHistorico("CANCELAMENTO_CONSULTA", consulta);
            
            log.info("Consulta ID: {} cancelada com sucesso.", id);
            return true;
        }).orElseThrow(() -> new IllegalArgumentException("Consulta não encontrada: " + id));
    }

    private void enviarNotificacao(String mensagem, Consulta consulta) {
        if (rabbitTemplate != null && exchange != null) {
            NotificacaoDTO notificacao = new NotificacaoDTO(
                    consulta.getId(),
                    consulta.getPacienteId(),
                    mensagem + " Para o dia: " + consulta.getDataHora().toString()
            );
            rabbitTemplate.convertAndSend(exchange.getName(), routingKey, notificacao);
            log.info("Notificação enviada via RabbitMQ para paciente ID: {} - Exchange: {}, Routing Key: {}", 
                    consulta.getPacienteId(), exchange.getName(), routingKey);
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
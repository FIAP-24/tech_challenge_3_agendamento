package com.challenge.agendamento.service;

import com.challenge.agendamento.dto.NotificacaoDTO;
import com.challenge.agendamento.model.Consulta;
import com.challenge.agendamento.repository.ConsultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final RabbitTemplate rabbitTemplate;
    private final Queue queue;

    @Transactional
    public Consulta registrarConsulta(Consulta consulta) {
        Consulta savedConsulta = consultaRepository.save(consulta);
        enviarNotificacao("Consulta agendada com sucesso!", savedConsulta);
        return savedConsulta;
    }

    @Transactional
    public Optional<Consulta> editarConsulta(Long id, Consulta consultaAtualizada) {
        return consultaRepository.findById(id).map(consultaExistente -> {
            consultaExistente.setDataHora(consultaAtualizada.getDataHora());
            consultaExistente.setDescricao(consultaAtualizada.getDescricao());
            Consulta savedConsulta = consultaRepository.save(consultaExistente);
            enviarNotificacao("Sua consulta foi remarcada.", savedConsulta);
            return savedConsulta;
        });
    }

    public List<Consulta> findConsultasByPacienteId(Long pacienteId) {
        return consultaRepository.findByPacienteId(pacienteId);
    }

    public List<Consulta> findProximasConsultasByPacienteId(Long pacienteId) {
        return consultaRepository.findByPacienteIdAndDataHoraAfter(pacienteId, LocalDateTime.now());
    }

    private void enviarNotificacao(String mensagem, Consulta consulta) {
        NotificacaoDTO notificacao = new NotificacaoDTO(
                consulta.getId(),
                consulta.getPacienteId(),
                mensagem + " Para o dia: " + consulta.getDataHora().toString()
        );
        rabbitTemplate.convertAndSend(queue.getName(), notificacao);
    }
}
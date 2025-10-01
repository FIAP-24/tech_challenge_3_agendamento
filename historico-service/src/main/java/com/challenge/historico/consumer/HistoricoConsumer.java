package com.challenge.historico.consumer;

import com.challenge.historico.dto.NotificacaoDTO;
import com.challenge.historico.model.HistoricoConsulta;
import com.challenge.historico.service.HistoricoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class HistoricoConsumer {

    private static final Logger log = LoggerFactory.getLogger(HistoricoConsumer.class);

    @Autowired
    private HistoricoService historicoService;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void onConsultaEvent(NotificacaoDTO dto) {
        log.info("Evento de histórico recebido para a consulta ID: {}", dto.getConsultaId());

        HistoricoConsulta historico = new HistoricoConsulta();
        historico.setConsultaId(dto.getConsultaId());
        historico.setPacienteId(dto.getPacienteId());
        historico.setEvento(dto.getMensagem());
        historico.setTimestamp(LocalDateTime.now());

        historicoService.salvarHistorico(historico);
    }
}
package com.rpgturnos.autenticacao.rabbitmq;

import com.rpgturnos.autenticacao.configuracao.RabbitMQConfig;
import com.rpgturnos.autenticacao.servico.HistoricoUsuarioService;
import com.rpgturnos.combate.event.BatalhaFinalizadaEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class BatalhaFinalizadaConsumer {

    private final HistoricoUsuarioService historicoUsuarioService;

    public BatalhaFinalizadaConsumer(HistoricoUsuarioService historicoUsuarioService) {
        this.historicoUsuarioService = historicoUsuarioService;
    }

    @RabbitListener(queues = RabbitMQConfig.FILA_BATALHA_FINALIZADA)
    public void consumir(BatalhaFinalizadaEvent event) {
        historicoUsuarioService.registrarPartidaFinalizada(event);
    }
}

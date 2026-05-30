package com.rpgturnos.autenticacao.rabbitmq;

import com.rpgturnos.autenticacao.configuracao.RabbitMQConfig;
import com.rpgturnos.autenticacao.servico.HistoricoUsuarioService;
import com.rpgturnos.combate.event.BatalhaFinalizadaEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "true")
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

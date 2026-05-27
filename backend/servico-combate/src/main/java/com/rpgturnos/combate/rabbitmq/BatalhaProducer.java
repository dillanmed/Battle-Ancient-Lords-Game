package com.rpgturnos.combate.rabbitmq;

import com.rpgturnos.combate.event.BatalhaFinalizadaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class BatalhaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatalhaProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public BatalhaProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarBatalhaFinalizada(BatalhaFinalizadaEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.FILA_BATALHA_FINALIZADA,
                    event
            );
        } catch (AmqpException exception) {
            LOGGER.warn("Nao foi possivel publicar o evento de batalha finalizada. batalhaId={}",
                    event.getBatalhaId(), exception);
        }
    }
}

package com.rpgturnos.combate.rabbitmq;

import com.rpgturnos.combate.event.BatalhaFinalizadaEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BatalhaProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(BatalhaProducer.class);

    private final ObjectProvider<RabbitTemplate> rabbitTemplateProvider;
    private final boolean rabbitMqEnabled;

    public BatalhaProducer(ObjectProvider<RabbitTemplate> rabbitTemplateProvider,
                           @Value("${app.rabbitmq.enabled:false}") boolean rabbitMqEnabled) {
        this.rabbitTemplateProvider = rabbitTemplateProvider;
        this.rabbitMqEnabled = rabbitMqEnabled;
    }

    public void publicarBatalhaFinalizada(BatalhaFinalizadaEvent event) {
        if (!rabbitMqEnabled) {
            return;
        }

        try {
            RabbitTemplate rabbitTemplate = rabbitTemplateProvider.getObject();
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

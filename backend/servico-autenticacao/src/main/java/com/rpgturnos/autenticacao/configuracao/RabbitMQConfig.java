package com.rpgturnos.autenticacao.configuracao;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "app.rabbitmq.enabled", havingValue = "true")
public class RabbitMQConfig {

    public static final String FILA_BATALHA_FINALIZADA = "batalha.finalizada";

    @Bean
    public Queue batalhaFinalizadaQueue() {
        return new Queue(FILA_BATALHA_FINALIZADA, true);
    }
}

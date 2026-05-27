package com.rpgturnos.combate.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String FILA_BATALHA_FINALIZADA = "batalha.finalizada";

    @Bean
    public Queue batalhaFinalizadaQueue() {
        return new Queue(FILA_BATALHA_FINALIZADA, true);
    }
}
package com.rpgturnos.combate.aspect;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("within(com.rpgturnos.combate.controller..*) || within(com.rpgturnos.combate.facade..*) || within(com.rpgturnos.combate.service..*)")
    public void logMetodoChamado(JoinPoint joinPoint) {
        LOGGER.info("Metodo chamado: {} args={}", joinPoint.getSignature().toShortString(),
                resumirArgumentos(joinPoint.getArgs()));
    }

    private String resumirArgumentos(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        String resumo = Arrays.toString(args);
        return resumo.length() <= 300 ? resumo : resumo.substring(0, 300) + "...";
    }
}

package com.rpgturnos.combate.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionTimeAspect.class);

    @Around("within(com.rpgturnos.combate.controller..*) || within(com.rpgturnos.combate.facade..*) || within(com.rpgturnos.combate.service..*)")
    public Object medirTempo(ProceedingJoinPoint joinPoint) throws Throwable {
        long inicio = System.currentTimeMillis();

        try {
            return joinPoint.proceed();
        } finally {
            long duracao = System.currentTimeMillis() - inicio;
            LOGGER.info("Tempo de execucao: {} levou {} ms", joinPoint.getSignature().toShortString(), duracao);
        }
    }
}

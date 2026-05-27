package com.rpgturnos.personagem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class HabilidadeServiceTest {

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() throws Exception {
        Object habilidadeRepository = applicationContext.getBean("habilidadeRepository");
        habilidadeRepository.getClass().getMethod("deleteAll").invoke(habilidadeRepository);
        Object dataInitializer = applicationContext.getBean("dataInitializer");
        dataInitializer.getClass().getMethod("run", String[].class).invoke(dataInitializer, (Object) new String[0]);
    }

    @Test
    void deveListarHabilidadesPorClasse() throws Exception {
        Object habilidadeService = applicationContext.getBean("habilidadeService");
        Class<?> classePersonagem = Class.forName("com.rpgturnos.personagem.model.ClassePersonagem");
        Object classe = Enum.valueOf((Class<Enum>) classePersonagem.asSubclass(Enum.class), "ARQUEIRO");
        Method listarPorClasse = habilidadeService.getClass().getMethod("listarPorClasse", classePersonagem);
        List<?> habilidades = (List<?>) listarPorClasse.invoke(habilidadeService, classe);

        assertThat(habilidades).hasSize(2);
        assertThat(habilidades).extracting(habilidade -> valor(habilidade, "getNome"))
                .contains("Flecha Precisa", "Disparo Duplo");
    }

    private Object valor(Object target, String methodName) {
        try {
            return target.getClass().getMethod(methodName).invoke(target);
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}

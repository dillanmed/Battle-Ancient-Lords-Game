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
class PersonagemServiceTest {

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeEach
    void setUp() throws Exception {
        Object personagemRepository = applicationContext.getBean("personagemRepository");
        Object habilidadeRepository = applicationContext.getBean("habilidadeRepository");
        personagemRepository.getClass().getMethod("deleteAll").invoke(personagemRepository);
        habilidadeRepository.getClass().getMethod("deleteAll").invoke(habilidadeRepository);
        Object dataInitializer = applicationContext.getBean("dataInitializer");
        dataInitializer.getClass().getMethod("run", String[].class).invoke(dataInitializer, (Object) new String[0]);
    }

    @Test
    void deveCriarBuscarEListarPersonagemPorUsuario() throws Exception {
        Object personagemService = applicationContext.getBean("personagemService");
        Object request = criarRequest("Luna", "MAGO");

        Object criado = criar(personagemService, request);
        Object encontrado = personagemService.getClass().getMethod("buscarPorId", Long.class)
                .invoke(personagemService, valor(criado, "getId"));
        List<?> personagens = (List<?>) personagemService.getClass().getMethod("listarPorUsuario", Long.class)
                .invoke(personagemService, 10L);

        assertThat(valor(encontrado, "getNome")).isEqualTo("Luna");
        assertThat(valor(encontrado, "getClasse").toString()).isEqualTo("MAGO");
        assertThat(personagens).hasSize(1);
    }

    @Test
    void deveRetornarDadosDeCombateComHabilidadesDisponiveis() throws Exception {
        Object personagemService = applicationContext.getBean("personagemService");
        Object criado = criar(personagemService, criarRequest("Ayla", "ARQUEIRO"));

        Object dadosCombate = personagemService.getClass().getMethod("buscarDadosCombate", Long.class)
                .invoke(personagemService, valor(criado, "getId"));

        assertThat(valor(dadosCombate, "getId")).isEqualTo(valor(criado, "getId"));
        assertThat(valor(dadosCombate, "getVidaMaxima")).isEqualTo(90);
        assertThat((List<?>) valor(dadosCombate, "getHabilidades")).hasSize(2);
    }

    private Object criar(Object personagemService, Object request) throws Exception {
        Method criar = personagemService.getClass().getMethod("criar",
                Class.forName("com.rpgturnos.personagem.dto.CriarPersonagemRequest"));
        return criar.invoke(personagemService, request);
    }

    private Object criarRequest(String nome, String classe) throws Exception {
        Class<?> requestClass = Class.forName("com.rpgturnos.personagem.dto.CriarPersonagemRequest");
        Class<?> classePersonagem = Class.forName("com.rpgturnos.personagem.model.ClassePersonagem");
        Object classeEnum = Enum.valueOf((Class<Enum>) classePersonagem.asSubclass(Enum.class), classe);
        Object request = requestClass.getConstructor().newInstance();
        requestClass.getMethod("setUsuarioId", Long.class).invoke(request, 10L);
        requestClass.getMethod("setNome", String.class).invoke(request, nome);
        requestClass.getMethod("setClasse", classePersonagem).invoke(request, classeEnum);
        return request;
    }

    private Object valor(Object target, String methodName) throws Exception {
        return target.getClass().getMethod(methodName).invoke(target);
    }
}

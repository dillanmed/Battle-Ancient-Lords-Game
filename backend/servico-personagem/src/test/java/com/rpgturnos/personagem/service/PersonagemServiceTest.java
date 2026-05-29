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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        assertThat(valor(dadosCombate, "getManaMaxima")).isEqualTo(60);
        assertThat(valor(dadosCombate, "getAtaque")).isEqualTo(16);
        assertThat(valor(dadosCombate, "getDefesa")).isEqualTo(12);
        assertThat(valor(dadosCombate, "getForca")).isEqualTo(12);
        assertThat(valor(dadosCombate, "getInteligencia")).isEqualTo(10);
        assertThat(valor(dadosCombate, "getAgilidade")).isEqualTo(18);
        assertThat((List<?>) valor(dadosCombate, "getHabilidades")).hasSize(2);
    }

    @Test
    void deveAdicionarExperienciaAoPersonagem() throws Exception {
        Object personagemService = applicationContext.getBean("personagemService");
        Object criado = criar(personagemService, criarRequest("Borin", "GUERREIRO"));
        Object request = criarAtualizarExperienciaRequest(80);

        Object atualizado = personagemService.getClass()
                .getMethod("atualizarExperiencia", Long.class,
                        Class.forName("com.rpgturnos.personagem.dto.AtualizarExperienciaRequest"))
                .invoke(personagemService, valor(criado, "getId"), request);

        assertThat(valor(atualizado, "getExperiencia")).isEqualTo(80);
    }

    @Test
    void deveEvoluirPersonagemConsumindoExperiencia() throws Exception {
        Object personagemService = applicationContext.getBean("personagemService");
        Object criado = criar(personagemService, criarRequest("Borin", "GUERREIRO"));
        Object request = criarAtualizarExperienciaRequest(120);
        personagemService.getClass()
                .getMethod("atualizarExperiencia", Long.class,
                        Class.forName("com.rpgturnos.personagem.dto.AtualizarExperienciaRequest"))
                .invoke(personagemService, valor(criado, "getId"), request);

        Object evoluido = personagemService.getClass().getMethod("evoluir", Long.class)
                .invoke(personagemService, valor(criado, "getId"));

        assertThat(valor(evoluido, "getNivel")).isEqualTo(2);
        assertThat(valor(evoluido, "getExperiencia")).isEqualTo(20);
        assertThat(valor(evoluido, "getVidaMaxima")).isEqualTo(135);
        assertThat(valor(evoluido, "getForca")).isEqualTo(21);
    }

    @Test
    void naoDeveEvoluirSemExperienciaSuficiente() throws Exception {
        Object personagemService = applicationContext.getBean("personagemService");
        Object criado = criar(personagemService, criarRequest("Luna", "MAGO"));
        Method evoluir = personagemService.getClass().getMethod("evoluir", Long.class);

        assertThatThrownBy(() -> evoluir.invoke(personagemService, valor(criado, "getId")))
                .hasCauseInstanceOf(IllegalArgumentException.class)
                .hasRootCauseMessage("Experiencia insuficiente para evoluir. Necessario: 100, atual: 0");
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

    private Object criarAtualizarExperienciaRequest(Integer experiencia) throws Exception {
        Class<?> requestClass = Class.forName("com.rpgturnos.personagem.dto.AtualizarExperienciaRequest");
        Object request = requestClass.getConstructor().newInstance();
        requestClass.getMethod("setExperiencia", Integer.class).invoke(request, experiencia);
        return request;
    }

    private Object valor(Object target, String methodName) throws Exception {
        return target.getClass().getMethod(methodName).invoke(target);
    }
}

package com.rpgturnos.personagem.factory;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersonagemFactoryTest {

    @Test
    void deveCriarGuerreiroComAtributosBase() throws Exception {
        Object personagem = criarPersonagem("Arthur", "GUERREIRO");

        assertThat(valor(personagem, "getClasse").toString()).isEqualTo("GUERREIRO");
        assertThat(valor(personagem, "getVidaMaxima")).isEqualTo(120);
        assertThat(valor(personagem, "getManaMaxima")).isEqualTo(30);
        assertThat((Integer) valor(personagem, "getForca")).isGreaterThan((Integer) valor(personagem, "getInteligencia"));
    }

    @Test
    void deveCriarMagoComManaAlta() throws Exception {
        Object personagem = criarPersonagem("Merlin", "MAGO");

        assertThat(valor(personagem, "getClasse").toString()).isEqualTo("MAGO");
        assertThat(valor(personagem, "getManaMaxima")).isEqualTo(120);
        assertThat((Integer) valor(personagem, "getInteligencia")).isGreaterThan((Integer) valor(personagem, "getForca"));
    }

    @Test
    void deveCriarArqueiroComAgilidadeAlta() throws Exception {
        Object personagem = criarPersonagem("Robin", "ARQUEIRO");

        assertThat(valor(personagem, "getClasse").toString()).isEqualTo("ARQUEIRO");
        assertThat(valor(personagem, "getVidaMaxima")).isEqualTo(90);
        assertThat((Integer) valor(personagem, "getAgilidade")).isGreaterThan((Integer) valor(personagem, "getDefesa"));
    }

    private Object criarPersonagem(String nome, String classe) throws Exception {
        Class<?> classePersonagem = Class.forName("com.rpgturnos.personagem.model.ClassePersonagem");
        Class<?> factory = Class.forName("com.rpgturnos.personagem.factory.PersonagemFactory");
        Object classeEnum = Enum.valueOf((Class<Enum>) classePersonagem.asSubclass(Enum.class), classe);
        return factory.getMethod("criar", Long.class, String.class, classePersonagem)
                .invoke(null, 1L, nome, classeEnum);
    }

    private Object valor(Object target, String methodName) throws Exception {
        return target.getClass().getMethod(methodName).invoke(target);
    }
}

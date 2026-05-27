package com.rpgturnos.personagem.config;

import com.rpgturnos.personagem.model.ClassePersonagem;
import com.rpgturnos.personagem.model.Habilidade;
import com.rpgturnos.personagem.model.TipoHabilidade;
import com.rpgturnos.personagem.repository.HabilidadeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final HabilidadeRepository habilidadeRepository;

    public DataInitializer(HabilidadeRepository habilidadeRepository) {
        this.habilidadeRepository = habilidadeRepository;
    }

    @Override
    public void run(String... args) {
        List<Habilidade> habilidades = List.of(
                habilidade("Golpe Pesado", "Ataque fisico forte contra um alvo.", ClassePersonagem.GUERREIRO,
                        TipoHabilidade.ATAQUE, 10, 28, 1),
                habilidade("Defesa de Ferro", "Aumenta a resistencia do guerreiro durante o combate.",
                        ClassePersonagem.GUERREIRO, TipoHabilidade.DEFESA, 15, 20, 1),
                habilidade("Bola de Fogo", "Ataque magico de fogo contra um alvo.", ClassePersonagem.MAGO,
                        TipoHabilidade.ATAQUE, 20, 35, 1),
                habilidade("Cura Arcana", "Recupera vida usando energia arcana.", ClassePersonagem.MAGO,
                        TipoHabilidade.CURA, 25, 30, 1),
                habilidade("Flecha Precisa", "Disparo certeiro com alta chance de dano.", ClassePersonagem.ARQUEIRO,
                        TipoHabilidade.ATAQUE, 10, 24, 1),
                habilidade("Disparo Duplo", "Dois ataques rapidos contra o inimigo.", ClassePersonagem.ARQUEIRO,
                        TipoHabilidade.ATAQUE, 20, 32, 1)
        );

        habilidades.stream()
                .filter(habilidade -> !habilidadeRepository.existsByNomeAndClassePermitida(habilidade.getNome(),
                        habilidade.getClassePermitida()))
                .forEach(habilidadeRepository::save);
    }

    private Habilidade habilidade(String nome, String descricao, ClassePersonagem classe, TipoHabilidade tipo,
                                  Integer custoMana, Integer poder, Integer nivelNecessario) {
        return Habilidade.builder()
                .nome(nome)
                .descricao(descricao)
                .classePermitida(classe)
                .tipo(tipo)
                .custoMana(custoMana)
                .poder(poder)
                .nivelNecessario(nivelNecessario)
                .build();
    }
}

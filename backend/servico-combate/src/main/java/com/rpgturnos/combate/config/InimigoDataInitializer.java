package com.rpgturnos.combate.config;

import com.rpgturnos.combate.model.Inimigo;
import com.rpgturnos.combate.repository.InimigoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class InimigoDataInitializer {

    @Bean
    CommandLineRunner inicializarInimigos(InimigoRepository inimigoRepository) {
        return args -> {
            if (inimigoRepository.count() > 0) {
                return;
            }

            inimigoRepository.saveAll(List.of(
                    inimigo("Goblin", 1, 1, 40, 8, 2, false),
                    inimigo("Slime", 1, 1, 35, 6, 3, false),
                    inimigo("Morcego", 1, 1, 30, 9, 1, false),
                    inimigo("Orc", 2, 2, 75, 14, 6, false),
                    inimigo("Lobo", 2, 2, 60, 16, 4, false),
                    inimigo("Esqueleto", 2, 2, 70, 13, 7, false),
                    inimigo("Esqueleto Warrior", 3, 3, 110, 21, 10, false),
                    inimigo("Dark Mage", 3, 3, 90, 26, 6, false),
                    inimigo("Golem", 3, 3, 120, 18, 15, false),
                    inimigo("Gorgon Dark", 4, 4, 150, 30, 14, false),
                    inimigo("Cavaleiro Sombrio", 4, 4, 160, 28, 18, false),
                    inimigo("Bruxa", 4, 4, 130, 34, 11, false),
                    inimigo("Wolfman", 5, 5, 190, 38, 18, false),
                    inimigo("Dragão Menor", 5, 5, 220, 42, 22, false),
                    inimigo("Executor", 5, 5, 210, 45, 20, false),
                    inimigo("Minotauro", 6, 6, 550, 60, 35, true)
            ));
        };
    }

    private Inimigo inimigo(String nome,
                            Integer fase,
                            Integer nivel,
                            Integer vidaMaxima,
                            Integer ataque,
                            Integer defesa,
                            Boolean boss) {
        return Inimigo.builder()
                .nome(nome)
                .fase(fase)
                .nivel(nivel)
                .vidaMaxima(vidaMaxima)
                .ataque(ataque)
                .defesa(defesa)
                .boss(boss)
                .build();
    }
}

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
            List<Inimigo> seeds = List.of(
                    inimigo("Goblin", 1, 1, 40, 8, 2, 20, "esqueleto", "COMUM", "Criatura inicial agil.", false),
                    inimigo("Slime", 1, 1, 35, 6, 3, 18, "esqueleto", "COMUM", "Criatura fraca da primeira fase.", false),
                    inimigo("Morcego", 1, 1, 30, 9, 1, 18, "esqueleto", "COMUM", "Inimigo rapido da primeira fase.", false),
                    inimigo("Orc", 2, 2, 75, 14, 6, 35, "esqueleto", "COMUM", "Inimigo fisico da segunda fase.", false),
                    inimigo("Lobo", 2, 2, 60, 16, 4, 32, "esqueleto", "COMUM", "Inimigo veloz da segunda fase.", false),
                    inimigo("Esqueleto", 2, 2, 70, 13, 7, 35, "esqueleto", "COMUM", "Morto-vivo basico.", false),
                    inimigo("Esqueleto Warrior", 3, 3, 110, 21, 10, 55, "esqueleto warrior", "COMUM", "Morto-vivo armado.", false),
                    inimigo("Dark Mage", 3, 3, 90, 26, 6, 55, "esqueleto", "COMUM", "Conjurador sombrio.", false),
                    inimigo("Golem", 3, 3, 120, 18, 15, 60, "esqueleto", "COMUM", "Criatura resistente.", false),
                    inimigo("Gorgon Dark", 4, 4, 150, 30, 14, 80, "Gorgon dark", "COMUM", "Inimigo sombrio avancado.", false),
                    inimigo("Cavaleiro Sombrio", 4, 4, 160, 28, 18, 85, "esqueleto", "COMUM", "Guerreiro corrompido.", false),
                    inimigo("Bruxa", 4, 4, 130, 34, 11, 80, "esqueleto", "COMUM", "Conjuradora da quarta fase.", false),
                    inimigo("Wolfman", 5, 5, 190, 38, 18, 120, "Wolfman", "COMUM", "Inimigo feroz da quinta fase.", false),
                    inimigo("Dragão Menor", 5, 5, 220, 42, 22, 135, "esqueleto", "COMUM", "Dragao jovem da quinta fase.", false),
                    inimigo("Executor", 5, 5, 210, 45, 20, 130, "esqueleto", "COMUM", "Carrasco da quinta fase.", false),
                    inimigo("Minotauro", 6, 6, 550, 60, 35, 300, "Minotauro", "BOSS", "Lorde ancestral da arena final.", true)
            );

            seeds.forEach(seed -> inimigoRepository.findByNomeAndFase(seed.getNome(), seed.getFase())
                    .ifPresentOrElse(
                            existente -> atualizarCamposGerenciados(existente, seed, inimigoRepository),
                            () -> inimigoRepository.save(seed)
                    ));
        };
    }

    private Inimigo inimigo(String nome,
                            Integer fase,
                            Integer nivel,
                            Integer vidaMaxima,
                            Integer ataque,
                            Integer defesa,
                            Integer recompensaXp,
                            String spriteKey,
                            String tipo,
                            String descricao,
                            Boolean boss) {
        return Inimigo.builder()
                .nome(nome)
                .fase(fase)
                .nivel(nivel)
                .vidaMaxima(vidaMaxima)
                .ataque(ataque)
                .defesa(defesa)
                .recompensaXp(recompensaXp)
                .ativo(true)
                .spriteKey(spriteKey)
                .tipo(tipo)
                .descricao(descricao)
                .boss(boss)
                .build();
    }

    private void atualizarCamposGerenciados(Inimigo existente, Inimigo seed, InimigoRepository inimigoRepository) {
        existente.setNivel(seed.getNivel());
        existente.setVidaMaxima(seed.getVidaMaxima());
        existente.setAtaque(seed.getAtaque());
        existente.setDefesa(seed.getDefesa());
        existente.setRecompensaXp(seed.getRecompensaXp());
        existente.setAtivo(true);
        existente.setSpriteKey(seed.getSpriteKey());
        existente.setTipo(seed.getTipo());
        existente.setDescricao(seed.getDescricao());
        existente.setBoss(seed.getBoss());
        inimigoRepository.save(existente);
    }
}

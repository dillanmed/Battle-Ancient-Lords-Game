package com.rpgturnos.combate.config;

import com.rpgturnos.combate.model.Inimigo;
import com.rpgturnos.combate.repository.InimigoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class InimigoDataInitializer {

    private static final Set<String> NOMES_GERENCIADOS_PELO_INITIALIZER = Set.of(
            "Goblin",
            "Slime",
            "Morcego",
            "Orc",
            "Lobo",
            "Esqueleto",
            "Esqueleto Warrior",
            "Dark Mage",
            "Golem",
            "Gorgon Dark",
            "Cavaleiro Sombrio",
            "Bruxa",
            "Wolfman",
            "Dragao Menor",
            "Dragão Menor",
            "Executor",
            "Medusa",
            "Minotauro"
    );

    @Bean
    CommandLineRunner inicializarInimigos(InimigoRepository inimigoRepository) {
        return args -> {
            List<Inimigo> seeds = List.of(
                    inimigo("Esqueleto", 1, 1, 40, 8, 2, 20, "esqueleto", "COMUM", "Sentinela osseo da primeira arena.", false),
                    inimigo("Esqueleto Warrior", 1, 1, 48, 10, 4, 24, "esqueleto warrior", "COMUM", "Guerreiro osseo de patrulha.", false),
                    inimigo("Medusa", 1, 1, 44, 9, 3, 24, "medusa", "COMUM", "Criatura venenosa da arena inicial.", false),

                    inimigo("Esqueleto Warrior", 2, 2, 75, 15, 7, 36, "esqueleto warrior", "COMUM", "Guerreiro osseo veterano.", false),
                    inimigo("Gorgon Dark", 2, 2, 82, 17, 6, 40, "Gorgon dark", "COMUM", "Guardia sombria das ruinas.", false),
                    inimigo("Medusa", 2, 2, 70, 18, 5, 38, "medusa", "COMUM", "Medusa mais agressiva.", false),

                    inimigo("Gorgon Dark", 3, 3, 120, 24, 10, 60, "Gorgon dark", "COMUM", "Gorgon fortalecida pela escuridao.", false),
                    inimigo("Medusa", 3, 3, 105, 26, 8, 58, "medusa", "COMUM", "Medusa de elite.", false),
                    inimigo("Wolfman", 3, 3, 125, 25, 9, 62, "Wolfman", "COMUM", "Cacador feroz da terceira arena.", false),

                    inimigo("Wolfman", 4, 4, 165, 33, 14, 90, "Wolfman", "COMUM", "Wolfman brutal.", false),
                    inimigo("Gorgon Dark", 4, 4, 155, 34, 13, 88, "Gorgon dark", "COMUM", "Gorgon sombria veterana.", false),
                    inimigo("Esqueleto Warrior", 4, 4, 170, 31, 17, 86, "esqueleto warrior", "COMUM", "Campeao osseo da quarta arena.", false),

                    inimigo("Wolfman", 5, 5, 220, 42, 20, 130, "Wolfman", "ELITE", "Predador alfa da penultima arena.", false),
                    inimigo("Minotauro", 5, 5, 260, 45, 24, 145, "Minotauro", "ELITE", "Minotauro guardiao.", false),
                    inimigo("Medusa", 5, 5, 205, 44, 18, 128, "medusa", "ELITE", "Medusa ancestral.", false),

                    inimigo("Minotauro", 6, 6, 550, 60, 35, 300, "Minotauro", "BOSS", "Lorde final da arena antiga.", true)
            );


            seeds.forEach(seed -> upsertInimigo(inimigoRepository, seed));
            desativarInimigosGerenciadosForaDaLista(inimigoRepository, seeds);
        };
    }

    private void upsertInimigo(InimigoRepository inimigoRepository, Inimigo seed) {
        Inimigo inimigo = inimigoRepository.findByNomeAndFase(seed.getNome(), seed.getFase())
                .orElseGet(Inimigo::new);

        inimigo.setNome(seed.getNome());
        inimigo.setFase(seed.getFase());
        inimigo.setNivel(seed.getNivel());
        inimigo.setVidaMaxima(seed.getVidaMaxima());
        inimigo.setAtaque(seed.getAtaque());
        inimigo.setDefesa(seed.getDefesa());
        inimigo.setBoss(seed.getBoss());
        inimigo.setRecompensaXp(seed.getRecompensaXp());
        inimigo.setAtivo(true);
        inimigo.setSpriteKey(seed.getSpriteKey());
        inimigo.setTipo(seed.getTipo());
        inimigo.setDescricao(seed.getDescricao());

        inimigoRepository.save(inimigo);
    }

    private void desativarInimigosGerenciadosForaDaLista(InimigoRepository inimigoRepository, List<Inimigo> seeds) {
        Set<String> chavesAtivas = seeds.stream()
                .map(seed -> chave(seed.getNome(), seed.getFase()))
                .collect(Collectors.toSet());

        inimigoRepository.findAll().stream()
                .filter(inimigo -> inimigo.getFase() != null && inimigo.getFase() >= 1 && inimigo.getFase() <= 6)
                .filter(inimigo -> NOMES_GERENCIADOS_PELO_INITIALIZER.contains(inimigo.getNome()))
                .filter(inimigo -> !chavesAtivas.contains(chave(inimigo.getNome(), inimigo.getFase())))
                .forEach(inimigo -> {
                    inimigo.setAtivo(false);
                    inimigoRepository.save(inimigo);
                });
    }

    private static String chave(String nome, Integer fase) {
        return fase + "::" + nome;
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


}

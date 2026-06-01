package game.combate

import game.Main
import game.effects.FloatingText
import game.services.ServiceRegistry
import game.types.Personagem

import java.awt.Color
import java.awt.image.BufferedImage
import java.util.Arrays

class BattleController extends Main {
    static Long xpSincronizadoNestaBatalha = null
    static volatile boolean battleActionInProgress = false
    static final long COUNTER_ATTACK_DELAY_MS = 300L
    static final long TURN_RESOLUTION_DELAY_MS = 300L

    static void executeClassSpecialSkill() {
        if (battleActionInProgress) return
        if (currentBattleId == null) {
            battleMessage = "Batalha nao iniciada no servidor de combate."
            return
        }
        if (enemyHP[selectedEnemy] <= 0 || enemyIds[selectedEnemy] == null) {
            battleMessage = "Selecione um alvo vivo para a habilidade especial."
            return
        }
        int custoMana = estimarCustoEspecial()
        if (playerMana < custoMana) {
            battleMessage = "Mana insuficiente para habilidade especial! Precisa de ${custoMana} MP."
            return
        }

        playerVisualState = STATE_ATTACK
        playerStateTime = System.currentTimeMillis()

        if (currentClass == "WARRIOR") {
            playSFX("src/main/resources/sons/grito-de-guerra.mp3")
        } else if (currentClass == "ARCHER") {
            playSFX("src/main/resources/sons/chuva de flechas.mp3")
        } else if (currentClass == "MAGE") {
            playSFX("src/main/resources/sons/Explosao.mp3")
        }

        processarHabilidadeBackend([
                inimigoId: enemyIds[selectedEnemy],
                tipo: "ESPECIAL"
        ], "Habilidade especial executada!")
    }

    static void processarHabilidadeBackend(Map habilidade, String mensagemSucesso) {
        int[] vidasAntes = Arrays.copyOf(enemyHP, enemyHP.length)
        int vidaJogadorAntes = playerHP
        int alvoIndex = indiceInimigoPorId(habilidade.inimigoId as Long)
        battleActionInProgress = true
        playerTurn = false

        if (alvoIndex >= 0 && enemyHP[alvoIndex] > 0) {
            battleMessage = "ESPECIAL! Atacando ${enemyNames[alvoIndex]}..."
        }

        Thread.start {
            Map batalha
            try {
                batalha = ServiceRegistry.combateService.usarHabilidade(currentBattleId, habilidade)
            } catch (Exception e) {
                battleMessage = "Erro ao usar habilidade no servidor de combate."
                println "Erro ao usar habilidade no servico-combate: ${e.message}"
                battleActionInProgress = false
                playerTurn = true
                return
            }

            aplicarInimigosBatalhaBackend(batalha)
            aplicarJogadorBatalhaBackend(batalha)
            battleMessage = mensagemEspecial(alvoIndex, vidasAntes, batalha, mensagemSucesso)

            for (int i = 0; i < enemyHP.length; i++) {
                if (vidasAntes[i] > enemyHP[i]) {
                    mostrarDanoNoInimigo(i, vidasAntes[i] - enemyHP[i])
                    if (enemyHP[i] <= 0) {
                        enemyVisualState[i] = STATE_DEAD
                        enemyStateTime[i] = System.currentTimeMillis()
                        grantKillRewards(i)
                        battleMessage = "${enemyNames[i]} derrotado!"
                    } else {
                        enemyVisualState[i] = STATE_HURT
                        enemyStateTime[i] = System.currentTimeMillis()
                    }
                }
            }

            Thread.sleep(COUNTER_ATTACK_DELAY_MS)
            int danoJogador = Math.max(vidaJogadorAntes - playerHP, 0)
            if (danoJogador > 0) {
                mostrarDanoNoJogador(danoJogador)
                if (playerHP > 0) {
                    playerVisualState = STATE_HURT
                    playerStateTime = System.currentTimeMillis()
                    BufferedImage currentPlayerFrame = getAnimationFrame(playerHurt, 100, false, playerStateTime, STATE_IDLE)
                    playerCachedHurtFrame = generateTintedSilhouette(currentPlayerFrame)
                    battleMessage = "Voce recebeu ${danoJogador} de dano!"
                }
            }

            Thread.sleep(TURN_RESOLUTION_DELAY_MS)
            resolverFimDeTurno(batalha)
        }
    }

    static void attackEnemy(int enemyIndex) {
        if (battleActionInProgress) return
        if (enemyHP[enemyIndex] <= 0) return
        if (currentBattleId == null || enemyIds[enemyIndex] == null) {
            battleMessage = "Batalha nao iniciada no servidor de combate."
            return
        }

        playerVisualState = STATE_ATTACK
        playerStateTime = System.currentTimeMillis()
        playerTurn = false
        battleActionInProgress = true

        int vidaInimigoAntes = enemyHP[enemyIndex]
        int vidaJogadorAntes = playerHP
        battleMessage = "Atacando ${enemyNames[enemyIndex]}..."

        Thread.start {
            Map batalha
            try {
                batalha = ServiceRegistry.combateService.atacar(currentBattleId, enemyIds[enemyIndex])
            } catch (Exception e) {
                battleMessage = "Erro ao atacar no servidor de combate."
                println "Erro ao atacar no servico-combate: ${e.message}"
                battleActionInProgress = false
                playerTurn = true
                return
            }

            aplicarInimigosBatalhaBackend(batalha)

            int damage = Math.max(vidaInimigoAntes - enemyHP[enemyIndex], 0)
            if (damage > 0) {
                mostrarDanoNoInimigo(enemyIndex, damage)
            }

            if (enemyHP[enemyIndex] <= 0) {
                enemyVisualState[enemyIndex] = STATE_DEAD
                enemyStateTime[enemyIndex] = System.currentTimeMillis()
                enemyCachedHurtFrames[enemyIndex] = null
                grantKillRewards(enemyIndex)
                battleMessage = "${enemyNames[enemyIndex]} derrotado!"
            } else {
                enemyVisualState[enemyIndex] = STATE_HURT
                enemyStateTime[enemyIndex] = System.currentTimeMillis()
                BufferedImage currentEnemyFrame = getEnemyAnimationFrame(enemyIndex, enemyHurts[enemyIndex], 100, false, enemyStateTime[enemyIndex])
                enemyCachedHurtFrames[enemyIndex] = generateTintedSilhouette(currentEnemyFrame)
                battleMessage = mensagemDanoJogador(enemyIndex, damage, false)
            }

            Thread.sleep(COUNTER_ATTACK_DELAY_MS)
            aplicarJogadorBatalhaBackend(batalha)
            int danoJogador = Math.max(vidaJogadorAntes - playerHP, 0)
            if (danoJogador > 0) {
                mostrarDanoNoJogador(danoJogador)
                if (playerHP > 0) {
                    playerVisualState = STATE_HURT
                    playerStateTime = System.currentTimeMillis()
                    BufferedImage currentPlayerFrame = getAnimationFrame(playerHurt, 100, false, playerStateTime, STATE_IDLE)
                    playerCachedHurtFrame = generateTintedSilhouette(currentPlayerFrame)
                    battleMessage = "Voce recebeu ${danoJogador} de dano!"
                }
            }

            Thread.sleep(TURN_RESOLUTION_DELAY_MS)
            resolverFimDeTurno(batalha)
        }
    }

    static int estimarCustoEspecial() {
        10 + (Math.max(playerLevel, 1) * 2)
    }

    static int indiceInimigoPorId(Long inimigoId) {
        if (inimigoId == null) return -1
        for (int i = 0; i < enemyIds.length; i++) {
            if (enemyIds[i] == inimigoId) return i
        }
        -1
    }

    static void mostrarDanoNoInimigo(int enemyIndex, int damage) {
        playSFX("src/main/resources/sons/Dano no inimigo.mp3")

        int damageX = (int) currentEnemyPos[enemyIndex][0] + 80
        int damageY = (int) currentEnemyPos[enemyIndex][1] - 20

        if (isBossFight && enemyIndex == 0) {
            damageX = WIDTH / 2
            damageY = 160
        }

        floatingTexts.add(new FloatingText("-" + Math.max(damage, 0), damageX, damageY, Color.RED))
    }

    static void mostrarDanoNoJogador(int damage) {
        playSFX("src/main/resources/sons/Dano-no-jogador.wav")
        floatingTexts.add(new FloatingText(
                "-" + Math.max(damage, 0),
                (int) currentPlayerX + 90,
                (int) currentPlayerY - 20,
                Color.ORANGE
        ))
    }

    static void aplicarJogadorBatalhaBackend(Map batalha) {
        if (!batalha) return
        if (batalha.jogadorVidaAtual != null) {
            playerHP = (batalha.jogadorVidaAtual as Number).intValue()
        }
        if (batalha.jogadorManaAtual != null) {
            playerMana = (batalha.jogadorManaAtual as Number).intValue()
        }
    }

    static void aplicarInimigosBatalhaBackend(Map batalha) {
        if (!batalha) return

        List inimigosBackend = (batalha.inimigos ?: []) as List
        for (int i = 0; i < 3; i++) {
            Map inimigo = inimigoBackendParaSlot(inimigosBackend, i)
            if (inimigo != null) {
                enemyIds[i] = inimigo.id == null ? null : inimigo.id as Long
                enemyNames[i] = inimigo.nome ?: enemyNames[i]
                enemyHP[i] = valorInteiro(inimigo.vidaAtual, enemyHP[i])
                enemyMaxHP[i] = valorInteiro(inimigo.vidaMaxima, enemyMaxHP[i])
                if (enemyHP[i] <= 0 || inimigo.vivo == false) {
                    enemyHP[i] = 0
                }
            } else {
                enemyIds[i] = null
                enemyHP[i] = 0
                enemyMaxHP[i] = 0
            }
        }

        maxEnemyHP = Math.max(1, enemyMaxHP.max() as int)
    }

    static String mensagemBackend(Map batalha, String fallback) {
        String mensagem = batalha?.mensagem?.toString()
        mensagem?.trim() ? mensagem : fallback
    }

    static String primeiraMensagemBackend(Map batalha, String fallback) {
        mensagemBackend(batalha, fallback).split("\\|")[0].trim()
    }

    static String mensagemDanoJogador(int enemyIndex, int damage, boolean especial) {
        String prefixo = especial ? "ESPECIAL! " : ""
        String critico = danoCritico(enemyIndex, damage) ? "CRITICO! " : ""
        "${prefixo}${critico}Voce causou ${damage} de dano em ${enemyNames[enemyIndex]}!"
    }

    static String mensagemEspecial(int enemyIndex, int[] vidasAntes, Map batalha, String fallback) {
        if (enemyIndex < 0) return primeiraMensagemBackend(batalha, fallback)

        int damage = Math.max(vidasAntes[enemyIndex] - enemyHP[enemyIndex], 0)
        if (damage <= 0) return primeiraMensagemBackend(batalha, fallback)

        mensagemDanoJogador(enemyIndex, damage, true)
    }

    static boolean danoCritico(int enemyIndex, int damage) {
        int maxHp = enemyIndex >= 0 && enemyIndex < enemyMaxHP.length ? Math.max(enemyMaxHP[enemyIndex], 1) : 1
        damage >= Math.max(18, (int)(maxHp * 0.35))
    }

    static void resolverFimDeTurno(Map batalha) {
        String resultado = batalha?.resultado?.toString()
        if (resultado == "VITORIA") {
            battleActionInProgress = false
            grantBattleVictoryRewards()
            if (currentPhase < 6) {
                startPhaseTransition()
            } else {
                stopAllMusic()
                selectedWinOpt = 0
                gameState = WIN
            }
        } else if (resultado == "DERROTA") {
            battleActionInProgress = false
            playerVisualState = STATE_DEAD
            playerStateTime = System.currentTimeMillis()
            battleMessage = "Fim de Jogo..."
            Thread.sleep(400)
            stopAllMusic()
            selectedGameOverOpt = 0
            gameState = GAME_OVER
        } else {
            battleActionInProgress = false
            playerTurn = true
        }
    }

    static void grantKillRewards(int enemyIndex) {
        int manaGanhar = 5 + random.nextInt(6)
        playerMana = Math.min(maxMana, playerMana + manaGanhar)
    }

    static void grantBattleVictoryRewards() {
        if (currentBattleId != null && xpSincronizadoNestaBatalha == currentBattleId) {
            println "XP ja sincronizado para esta batalha. Ignorando chamada duplicada."
            return
        }
        xpSincronizadoNestaBatalha = currentBattleId

        int inimigosDaBatalha = Math.max(1, enemyIds.count { it != null })
        double randomPercent = (0.25 + (random.nextDouble() * 0.25)) * inimigosDaBatalha
        int xpGanho = (int) (maxXP * randomPercent)
        playerXP += xpGanho

        if (playerXP >= maxXP) {
            playerXP -= maxXP
            playerLevel++
            damageBonus += 5
            maxXP = (int)(maxXP * 1.3)
            playerHP = Math.min(maxHP, playerHP + 30)
            playerMana = Math.min(maxMana, playerMana + 15)
        }

        sincronizarXpVitoriaComBackend(xpGanho)
    }

    static void sincronizarXpVitoriaComBackend(int xpGanho) {
        if (!personagemBackendCarregado || currentPersonagemId == null || currentPersonagemId <= 0L) {
            println "Falha ao sincronizar XP. Mantendo progressao local."
            return
        }

        try {
            println "Vitoria detectada. Sincronizando XP com servico-personagem..."
            Personagem personagemAtualizado = ServiceRegistry.personagemService.sincronizarExperienciaEvolucao(
                    currentPersonagemId.toString(),
                    xpGanho
            )

            if (personagemAtualizado == null) {
                println "Falha ao sincronizar XP. Mantendo progressao local."
                return
            }

            applyFrontendCharacter(personagemAtualizado)
            println "XP sincronizado com sucesso."
        } catch (Exception e) {
            println "Falha ao sincronizar XP. Mantendo progressao local."
            println "Erro ao sincronizar XP com servico-personagem: ${e.message}"
        }
    }

    static void enemyTurn() {
        if (playerHP <= 0 || isRespawning) return
        def aliveEnemies = []
        for (int i = 0; i < enemyHP.length; i++) {
            if (enemyHP[i] > 0) aliveEnemies.add(i)
        }
        if (aliveEnemies.isEmpty()) return

        int attacker = aliveEnemies.get(random.nextInt(aliveEnemies.size()))
        enemyVisualState[attacker] = STATE_ATTACK
        enemyStateTime[attacker] = System.currentTimeMillis()

        if (random.nextInt(100) < 15) {
            battleMessage = "O ${enemyNames[attacker]} TENTOU atacar, mas ERROU o golpe!"
            Thread.start {
                Thread.sleep(300)
                if (playerHP > 0) playerTurn = true
            }
            return
        }

        int damage = 5 + random.nextInt(10) + currentPhase
        playerHP -= damage
        playSFX("src/main/resources/sons/Dano-no-jogador.wav")

        floatingTexts.add(
                new FloatingText(
                        "-" + damage,
                        (int) currentPlayerX + 90,
                        (int) currentPlayerY - 20,
                        Color.ORANGE
                )
        )

        if (playerHP <= 0) {
            playerHP = 0
            playerVisualState = STATE_DEAD
            playerStateTime = System.currentTimeMillis()
            playerCachedHurtFrame = null
            playerLives--

            if (playerLives > 0) {
                battleMessage = "Voce morreu! Consumindo uma vida..."
                Thread.start {
                    Thread.sleep(400)
                    playSFX("src/main/resources/sons/respawnw.wav")
                    playerHP = maxHP
                    playerVisualState = STATE_IDLE
                    playerStateTime = System.currentTimeMillis()
                    isRespawning = true
                    respawnStartTime = System.currentTimeMillis()
                    Thread.sleep(RESPAWN_BLINK_DURATION)
                    playerTurn = true
                }
            } else {
                battleMessage = "Fim de Jogo..."
                Thread.start {
                    Thread.sleep(400)
                    stopAllMusic()
                    selectedGameOverOpt = 0
                    gameState = GAME_OVER
                }
            }
            return
        } else {
            playerVisualState = STATE_HURT
            playerStateTime = System.currentTimeMillis()
            BufferedImage currentPlayerFrame = getAnimationFrame(playerHurt, 100, false, playerStateTime, STATE_IDLE)
            playerCachedHurtFrame = generateTintedSilhouette(currentPlayerFrame)
            battleMessage = "O ${enemyNames[attacker]} atacou e tirou ${damage} de vida!"
        }

        Thread.start {
            Thread.sleep(300)
            if (playerHP > 0) playerTurn = true
        }
    }
}

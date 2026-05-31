package game.combate

import game.Main
import game.effects.FloatingText
import game.services.ServiceRegistry

import java.awt.Color
import java.awt.image.BufferedImage

class BattleController extends Main {

    static void executeClassSpecialSkill() {
        if (currentBattleId == null) {
            battleMessage = "Batalha nao iniciada no servidor de combate."
            return
        }

        if (currentClass == "WARRIOR") {
            if (playerMana < 15) {
                battleMessage = "Mana insuficiente para Grito de Guerra! (Precisa de 15 MP)"
                return
            }
            playerVisualState = STATE_ATTACK
            playerStateTime = System.currentTimeMillis()

            playSFX("src/main/resources/sons/grito-de-guerra.mp3")
            processarHabilidadeBackend([
                    nome: "Grito de Guerra",
                    tipo: "CURA",
                    dano: 25 + (playerLevel * 5),
                    custoMana: 15
            ], "Guerreiro ativou [GRITO DE GUERRA] Lv.${playerLevel}!")
            return

        } else if (currentClass == "ARCHER") {
            if (playerMana < 20) {
                battleMessage = "Mana insuficiente para Chuva de Flechas! (Precisa de 20 MP)"
                return
            }
            playerVisualState = STATE_ATTACK
            playerStateTime = System.currentTimeMillis()

            playSFX("src/main/resources/sons/chuva de flechas.mp3")
            processarHabilidadeBackend([
                    nome: "Chuva de Flechas",
                    tipo: "AREA",
                    dano: 8,
                    custoMana: 20
            ], "Arqueiro conjurou [CHUVA DE FLECHAS]!")
            return

        } else if (currentClass == "MAGE") {
            if (playerMana < 35) {
                battleMessage = "Mana insuficiente para Meteoro Arcano! (Precisa de 35 MP)"
                return
            }
            if (enemyHP[selectedEnemy] <= 0) {
                battleMessage = "Selecione um alvo vivo para o Meteoro!"
                return
            }
            playerVisualState = STATE_ATTACK
            playerStateTime = System.currentTimeMillis()

            playSFX("src/main/resources/sons/Explosao.mp3")
            processarHabilidadeBackend([
                    nome: "Meteoro Arcano",
                    tipo: "MAGICA",
                    dano: 20,
                    custoMana: 35,
                    inimigoId: enemyIds[selectedEnemy]
            ], "Mago invocou [METEORO ARCANO]!")
            return
        }
    }

    static void processarHabilidadeBackend(Map habilidade, String mensagemSucesso) {
        int[] vidasAntes = enemyHP.clone()
        int vidaJogadorAntes = playerHP
        Map batalha

        try {
            batalha = ServiceRegistry.combateService.usarHabilidade(currentBattleId, habilidade)
        } catch (Exception e) {
            battleMessage = "Erro ao usar habilidade no servidor de combate."
            println "Erro ao usar habilidade no servico-combate: ${e.message}"
            return
        }

        aplicarEstadoBatalhaBackend(batalha)
        battleMessage = mensagemSucesso

        for (int i = 0; i < enemyHP.length; i++) {
            if (vidasAntes[i] > enemyHP[i]) {
                if (enemyHP[i] <= 0) {
                    enemyVisualState[i] = STATE_DEAD
                    enemyStateTime[i] = System.currentTimeMillis()
                    grantKillRewards(i)
                } else {
                    enemyVisualState[i] = STATE_HURT
                    enemyStateTime[i] = System.currentTimeMillis()
                }
            }
        }

        if (vidaJogadorAntes > playerHP && playerHP > 0) {
            playerVisualState = STATE_HURT
            playerStateTime = System.currentTimeMillis()
        }

        playerTurn = false
        Thread.start {
            Thread.sleep(1500)
            String resultado = batalha?.resultado?.toString()
            if (resultado == "VITORIA") {
                if (currentPhase < 6) {
                    startPhaseTransition()
                } else {
                    stopAllMusic()
                    selectedWinOpt = 0
                    gameState = WIN
                }
            } else if (resultado == "DERROTA") {
                playerVisualState = STATE_DEAD
                playerStateTime = System.currentTimeMillis()
                battleMessage = "Fim de Jogo..."
                Thread.sleep(1200)
                stopAllMusic()
                selectedGameOverOpt = 0
                gameState = GAME_OVER
            } else {
                playerTurn = true
            }
        }
    }

    static void attackEnemy(int enemyIndex) {
        if (enemyHP[enemyIndex] <= 0) return
        if (currentBattleId == null || enemyIds[enemyIndex] == null) {
            battleMessage = "Batalha nao iniciada no servidor de combate."
            return
        }

        playerVisualState = STATE_ATTACK
        playerStateTime = System.currentTimeMillis()

        int vidaInimigoAntes = enemyHP[enemyIndex]
        int vidaJogadorAntes = playerHP
        Map batalha

        try {
            batalha = ServiceRegistry.combateService.atacar(currentBattleId, enemyIds[enemyIndex])
        } catch (Exception e) {
            battleMessage = "Erro ao atacar no servidor de combate."
            println "Erro ao atacar no servico-combate: ${e.message}"
            return
        }

        aplicarEstadoBatalhaBackend(batalha)

        int damage = Math.max(vidaInimigoAntes - enemyHP[enemyIndex], 0)
        int danoJogador = Math.max(vidaJogadorAntes - playerHP, 0)
        playSFX("src/main/resources/sons/Dano no inimigo.mp3")

        int damageX = (int) currentEnemyPos[enemyIndex][0] + 80
        int damageY = (int) currentEnemyPos[enemyIndex][1] - 20

        if (isBossFight && enemyIndex == 0) {
            damageX = WIDTH / 2
            damageY = 160
        }

        floatingTexts.add(
                new FloatingText(
                        "-" + damage,
                        damageX,
                        damageY,
                        Color.RED
                )
        )

        if (enemyHP[enemyIndex] <= 0) {
            enemyVisualState[enemyIndex] = STATE_DEAD
            enemyStateTime[enemyIndex] = System.currentTimeMillis()
            enemyCachedHurtFrames[enemyIndex] = null
            grantKillRewards(enemyIndex)
        } else {
            enemyVisualState[enemyIndex] = STATE_HURT
            enemyStateTime[enemyIndex] = System.currentTimeMillis()
            BufferedImage currentEnemyFrame = getEnemyAnimationFrame(enemyIndex, enemyHurts[enemyIndex], 100, false, enemyStateTime[enemyIndex])
            enemyCachedHurtFrames[enemyIndex] = generateTintedSilhouette(currentEnemyFrame)
            battleMessage = "Voce causou ${damage} de dano no inimigo!"
        }

        if (danoJogador > 0) {
            playSFX("src/main/resources/sons/Dano-no-jogador.wav")
            floatingTexts.add(
                    new FloatingText(
                            "-" + danoJogador,
                            (int) currentPlayerX + 90,
                            (int) currentPlayerY - 20,
                            Color.ORANGE
                    )
            )
            if (playerHP > 0) {
                playerVisualState = STATE_HURT
                playerStateTime = System.currentTimeMillis()
                BufferedImage currentPlayerFrame = getAnimationFrame(playerHurt, 100, false, playerStateTime, STATE_IDLE)
                playerCachedHurtFrame = generateTintedSilhouette(currentPlayerFrame)
                battleMessage = "Voce causou ${damage}. Contra-ataque tirou ${danoJogador} de vida!"
            }
        }

        playerTurn = false

        Thread.start {
            Thread.sleep(1500)
            String resultado = batalha?.resultado?.toString()
            if (resultado == "VITORIA") {
                if (currentPhase < 6) {
                    startPhaseTransition()
                } else {
                    stopAllMusic()
                    selectedWinOpt = 0
                    gameState = WIN
                }
            } else if (resultado == "DERROTA") {
                playerVisualState = STATE_DEAD
                playerStateTime = System.currentTimeMillis()
                battleMessage = "Fim de Jogo..."
                Thread.sleep(1200)
                stopAllMusic()
                selectedGameOverOpt = 0
                gameState = GAME_OVER
            } else {
                playerTurn = true
            }
        }
    }

    static void grantKillRewards(int enemyIndex) {
        int manaGanhar = 5 + random.nextInt(6)
        playerMana = Math.min(maxMana, playerMana + manaGanhar)

        double randomPercent = 0.25 + (random.nextDouble() * 0.25)
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
                Thread.sleep(1500)
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
                    Thread.sleep(1800)
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
                    Thread.sleep(1800)
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
            Thread.sleep(1200)
            if (playerHP > 0) playerTurn = true
        }
    }
}

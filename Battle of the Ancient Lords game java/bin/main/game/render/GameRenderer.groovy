package game.render

import game.Main
import game.effects.FloatingText

import java.awt.AlphaComposite
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Composite
import java.awt.Font
import java.awt.GradientPaint
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.util.Iterator
import java.util.List

class GameRenderer extends Main {

    static void renderMenu(Graphics2D g) {
        if (menuBackground != null) g.drawImage(menuBackground, 0, 0, WIDTH, HEIGHT, null)
        else { g.setColor(Color.BLACK); g.fillRect(0, 0, WIDTH, HEIGHT); }

        g.setColor(Color.WHITE)
        g.setFont(getGameFont(Font.BOLD, 54f))
        g.drawString("BATTLE OF THE", WIDTH / 2 - 250, 180)
        g.drawString("ANCIENT LORDS", WIDTH / 2 - 290, 260)

        for (int i = 0; i < menuOptions.length; i++) {
            g.setColor(selectedMenu == i ? Color.YELLOW : Color.WHITE)
            g.setFont(getGameFont(Font.BOLD, 36f))
            g.drawString(menuOptions[i], WIDTH / 2 - 140, 420 + (i * 80))
        }
    }

    static void renderCharacterSelect(Graphics2D g) {
        if (menuBackground != null) g.drawImage(menuBackground, 0, 0, WIDTH, HEIGHT, null)
        else { g.setColor(Color.BLACK); g.fillRect(0, 0, WIDTH, HEIGHT); }

        g.setColor(Color.WHITE)
        g.setFont(getGameFont(Font.BOLD, 52f))
        g.drawString("SELECT YOUR CLASS", WIDTH / 2 - 300, 100)

        int startX = WIDTH / 2 - 380
        for (int i = 0; i < characters.length; i++) {
            int x = startX + (i * 320)
            int displayHP = 0, displayMP = 0
            List<BufferedImage> previewList = null

            if (characters[i] == "WARRIOR") { displayHP = 120; displayMP = 30; previewList = menuPreviewWarrior }
            else if (characters[i] == "ARCHER") { displayHP = 90; displayMP = 60; previewList = menuPreviewArcher }
            else if (characters[i] == "MAGE") { displayHP = 70; displayMP = 120; previewList = menuPreviewMage }

            g.setColor(selectedCharacter == i ? new Color(255, 215, 0) : new Color(80, 80, 100))
            g.fillRoundRect(x, 220, 220, 320, 30, 30)
            g.setColor(new Color(30, 30, 50))
            g.fillRoundRect(x + 5, 225, 210, 310, 30, 30)

            g.setColor(Color.WHITE)
            g.setFont(getGameFont(Font.BOLD, 32f))
            g.drawString(characters[i], x + 25, 300)

            g.setColor(new Color(60, 60, 90))
            g.setStroke(new BasicStroke(2))
            g.drawLine(x + 25, 330, x + 195, 330)

            g.setFont(getGameFont(Font.BOLD, 22f))
            g.setColor(new Color(255, 90, 90))
            g.drawString("HP: " + displayHP, x + 35, 370)
            g.setColor(new Color(90, 160, 255))
            g.drawString("MP: " + displayMP, x + 35, 410)

            if (previewList != null && !previewList.isEmpty()) {
                long frameIndexLong = (System.currentTimeMillis() / 120) as long
                int index = (int) (frameIndexLong % previewList.size())
                BufferedImage currentFrame = previewList.get(index)
                if (currentFrame != null && currentFrame.getWidth() > 1) {
                    g.drawImage(currentFrame, x + 50, 420, 120, 120, null)
                }
            }
        }
    }

    static void renderGameOver(Graphics2D g) {
        g.setColor(new Color(10, 5, 5, 245))
        g.fillRect(0, 0, WIDTH, HEIGHT)

        g.setColor(new Color(220, 30, 30))
        g.setFont(getGameFont(Font.BOLD, 72f))
        g.drawString("GAME OVER", WIDTH / 2 - 200, HEIGHT / 3)

        for (int i = 0; i < gameOverOptions.length; i++) {
            g.setColor(selectedGameOverOpt == i ? Color.YELLOW : Color.WHITE)
            g.setFont(getGameFont(Font.BOLD, 32f))
            g.drawString(gameOverOptions[i], WIDTH / 2 - 150, HEIGHT / 2 + 60 + (i * 70))
        }
    }

    static void renderWin(Graphics2D g) {

        g.setColor(new Color(10, 10, 20))
        g.fillRect(0, 0, WIDTH, HEIGHT)

        g.setColor(new Color(255, 215, 0))

        try {
            g.setFont(getGameFont(Font.BOLD, 76f))
        } catch(Exception e) {
            g.setFont(new Font("Arial", Font.BOLD, 76))
        }

        g.drawString("VICTORY!", WIDTH / 2 - 220, HEIGHT / 3)

        g.setColor(Color.WHITE)

        try {
            g.setFont(getGameFont(Font.PLAIN, 28f))
        } catch(Exception e) {
            g.setFont(new Font("Arial", Font.PLAIN, 28))
        }

        g.drawString(
                "Voce derrotou o Minotauro Ancient Lord!",
                WIDTH / 2 - 320,
                HEIGHT / 3 + 80
        )

        for (int i = 0; i < winOptions.length; i++) {

            g.setColor(selectedWinOpt == i ? Color.YELLOW : Color.WHITE)

            try {
                g.setFont(getGameFont(Font.BOLD, 30f))
            } catch(Exception e) {
                g.setFont(new Font("Arial", Font.BOLD, 30))
            }

            g.drawString(
                    winOptions[i],
                    WIDTH / 2 - 140,
                    HEIGHT / 2 + (i * 70)
            )
        }
    }

    static void renderInventory(Graphics2D g) {
        // Renderiza o fundo de batalha levemente escurecido por trÃƒÂ¡s do inventÃƒÂ¡rio
        int bgIndex = Math.min(Math.max(0, currentPhase - 1), 5)
        if (phaseBackgrounds[bgIndex] != null) g.drawImage(phaseBackgrounds[bgIndex], 0, 0, WIDTH, HEIGHT, null)

        g.setColor(new Color(0, 0, 0, 160))
        g.fillRect(0, 0, WIDTH, HEIGHT)

        // Moldura do painel central
        int invWidth = 650
        int invHeight = 500
        int invX = (WIDTH - invWidth) / 2
        int invY = (HEIGHT - invHeight) / 2

        g.setColor(new Color(25, 25, 35, 245))
        g.fillRoundRect(invX, invY, invWidth, invHeight, 20, 20)
        g.setColor(new Color(130, 110, 75)) // Borda Dourada Envelhecida
        g.setStroke(new BasicStroke(4))
        g.drawRoundRect(invX, invY, invWidth, invHeight, 20, 20)

        // TÃƒÂ­tulo do Painel
        g.setColor(Color.ORANGE)
        g.setFont(getGameFont(Font.BOLD, 38f))
        g.drawString("BAU DE ITENS (INVENTARIO)", invX + 65, invY + 60)

        g.setColor(Color.GRAY)
        g.setStroke(new BasicStroke(2))
        g.drawLine(invX + 40, invY + 85, invX + invWidth - 40, invY + 85)

        // Render da lista de slots/itens (Estilo Grid em lista)
        g.setFont(getGameFont(Font.BOLD, 22f))
        for (int i = 0; i < inventoryItems.size(); i++) {
            int slotY = invY + 120 + (i * 50)

            // Fundo do slot
            g.setColor(new Color(45, 45, 55, 200))
            g.fillRoundRect(invX + 50, slotY, invWidth - 100, 42, 8, 8)
            g.setColor(new Color(75, 75, 95))
            g.drawRoundRect(invX + 50, slotY, invWidth - 100, 42, 8, 8)

            // Indicador GenÃƒÂ©rico de Sprite (ÃƒÂcone falso)
            g.setColor(Color.LIGHT_GRAY)
            g.fillRect(invX + 65, slotY + 9, 24, 24)
            g.setColor(Color.BLACK)
            g.drawRect(invX + 65, slotY + 9, 24, 24)

            // Texto do Item e sua quantidade
            g.setColor(Color.WHITE)
            g.drawString(inventoryItems[i], invX + 110, slotY + 28)

            g.setColor(Color.YELLOW)
            g.drawString("x" + inventoryQuantities[i], invX + invWidth - 110, slotY + 28)
        }

        // RodapÃƒÂ© de instruÃƒÂ§ÃƒÂµes
        g.setColor(Color.LIGHT_GRAY)
        g.setFont(getGameFont(Font.PLAIN, 18f))
        g.drawString("Pressione 'N' para fechar e retornar a batalha", invX + 115, invY + invHeight - 30)
    }

    static void renderBattle(Graphics2D g) {

        int bgIndex = Math.min(Math.max(0, currentPhase - 1), 5)

        if (phaseBackgrounds[bgIndex] != null) {
            g.drawImage(phaseBackgrounds[bgIndex], 0, 0, WIDTH, HEIGHT, null)
        }

        int hudY = HEIGHT - 240
        int spriteSize = 200

        int pX = (int) currentPlayerX
        int pY = (int) currentPlayerY

        // =========================================
        // PLAYER
        // =========================================

        if (playerHP > 0 || (System.currentTimeMillis() - playerStateTime < DEATH_FADE_DURATION)) {
            g.setColor(new Color(0,0,0,80))
            g.fillOval(pX + 45, pY + 180, 110, 20)
        }

        boolean pLoop = true
        List<BufferedImage> pTargetList = playerIdle

        if (playerHP <= 0) {
            pTargetList = playerDead
            pLoop = false
        } else {
            switch (playerVisualState) {
                case STATE_ATTACK:
                    pTargetList = playerAttack
                    pLoop = false
                    break

                case STATE_HURT:
                    pTargetList = playerHurt
                    pLoop = false
                    break

                case STATE_WALK:
                    pTargetList = playerWalk
                    break

                default:
                    pTargetList = playerIdle
                    break
            }
        }

        BufferedImage playerFrame =
                getAnimationFrame(
                        pTargetList,
                        100,
                        pLoop,
                        playerStateTime,
                        STATE_IDLE
                )

        drawSpriteWithEffects(
                g,
                playerFrame,
                pX,
                pY,
                spriteSize,
                spriteSize,
                playerStateTime,
                playerVisualState,
                playerCachedHurtFrame,
                true
        )

        // =========================================
        // INIMIGOS
        // =========================================

        for (int i = 0; i < 3; i++) {

            if (enemyHP[i] <= 0 && !isBossFight) continue

            int ex = (int) currentEnemyPos[i][0]
            int ey = (int) currentEnemyPos[i][1]

            // =========================================
// SELETOR AMARELO DO ALVO
// =========================================

            if (
                    selectedEnemy == i &&
                            playerTurn &&
                            enemyHP[i] > 0 &&
                            !introActive &&
                            !phaseTransitionActive
            ) {

                int selectorX = ex + 30
                int selectorY = ey + 175
                int selectorW = 140
                int selectorH = 30

                // Boss usa seletor maior
                if (isBossFight && i == 0) {
                    selectorX = ex + 70
                    selectorY = ey + 300
                    selectorW = 200
                    selectorH = 40
                }

                g.setColor(new Color(255,255,0,45))
                g.fillOval(
                        selectorX,
                        selectorY,
                        selectorW,
                        selectorH
                )

                g.setColor(Color.YELLOW)

                g.setStroke(new BasicStroke(2.5f))

                g.drawOval(
                        selectorX,
                        selectorY,
                        selectorW,
                        selectorH
                )
            }

            if (isBossFight && i == 0) {
                ex = WIDTH / 2 - 170
                ey = 180
                spriteSize = 340
            }

            List<BufferedImage> eTargetList
            boolean eLoop = true

            if (enemyHP[i] <= 0) {
                enemyVisualState[i] = STATE_DEAD
                eTargetList = enemyDeads[i]
                eLoop = false
            } else {

                switch (enemyVisualState[i]) {

                    case STATE_ATTACK:
                        eTargetList = enemyAttacks[i]
                        eLoop = false
                        break

                    case STATE_HURT:
                        eTargetList = enemyHurts[i]
                        eLoop = false
                        break

                    case STATE_WALK:
                        eTargetList = enemyWalks[i]
                        break

                    default:
                        eTargetList = enemyIdles[i]
                        break
                }
            }

            BufferedImage enemyFrame =
                    getEnemyAnimationFrame(
                            i,
                            eTargetList,
                            100,
                            eLoop,
                            enemyStateTime[i]
                    )

            drawSpriteWithEffects(
                    g,
                    enemyFrame,
                    ex + spriteSize,
                    ey,
                    -spriteSize,
                    spriteSize,
                    enemyStateTime[i],
                    enemyVisualState[i],
                    enemyCachedHurtFrames[i],
                    false
            )

            // =========================================
            // BARRA NORMAL DOS INIMIGOS
            // =========================================

            if (!isBossFight && enemyHP[i] > 0) {

                int barWidth = 110
                int barX = ex + (spriteSize - barWidth) / 2

                g.setColor(Color.DARK_GRAY)
                g.fillRoundRect(barX, ey - 15, barWidth, 10, 5, 5)

                g.setColor(new Color(220,50,50))

                int enemyHpWidth =
                        (int)((enemyHP[i] / (double) maxEnemyHP) * barWidth)

                g.fillRoundRect(
                        barX,
                        ey - 15,
                        Math.max(0, enemyHpWidth),
                        10,
                        5,
                        5
                )

                g.setColor(Color.WHITE)
                g.setFont(getGameFont(Font.BOLD, 13f))

                g.drawString(enemyNames[i], barX + 5, ey - 22)
            }
        }

        // =========================================
        // BOSS BAR TOPO DA TELA
        // =========================================

        if (isBossFight) {

            int barWidth = 700
            int barHeight = 32

            int barX = (WIDTH / 2) - (barWidth / 2)
            int barY = 30

            g.setColor(new Color(20,20,20,230))
            g.fillRoundRect(barX, barY, barWidth, barHeight, 14, 14)

            g.setColor(new Color(180,20,20))

            int hpWidth =
                    (int)((enemyHP[0] / (double) bossMaxHP) * barWidth)

            g.fillRoundRect(
                    barX,
                    barY,
                    hpWidth,
                    barHeight,
                    14,
                    14
            )

            g.setColor(Color.WHITE)

            g.setFont(getGameFont(Font.BOLD, 28f))

            String bossText =
                    bossName +
                            "  " +
                            enemyHP[0] +
                            " / " +
                            bossMaxHP

            int textWidth = g.getFontMetrics().stringWidth(bossText)

            g.drawString(
                    bossText,
                    WIDTH / 2 - textWidth / 2,
                    23
            )
        }

        // =========================================
        // FLOATING DAMAGE TEXT
        // =========================================

        Iterator<FloatingText> iterator = floatingTexts.iterator()

        while (iterator.hasNext()) {

            FloatingText txt = iterator.next()

            long elapsed =
                    System.currentTimeMillis() - txt.startTime

            if (elapsed > 1000) {
                iterator.remove()
                continue
            }

            int alpha = 255 - (int)(elapsed / 4)

            g.setColor(
                    new Color(
                            txt.color.red,
                            txt.color.green,
                            txt.color.blue,
                            Math.max(alpha, 0)
                    )
            )

            g.setFont(getGameFont(Font.BOLD, 28f))

            g.drawString(
                    txt.text,
                    txt.x,
                    txt.y - (int)(elapsed / 20)
            )
        }

        // =========================================
        // HUD
        // =========================================

        g.setColor(new Color(15,15,20,240))
        g.fillRoundRect(30, hudY, WIDTH - 60, 120, 15, 15)

        g.setColor(new Color(90,90,110))
        g.setStroke(new BasicStroke(3))
        g.drawRoundRect(30, hudY, WIDTH - 60, 120, 15, 15)

        // HP
        g.setFont(getGameFont(Font.BOLD, 18f))
        g.setColor(new Color(255,70,70))
        g.drawString("HP", 60, hudY + 50)

        g.setColor(new Color(45,45,45))
        g.fillRoundRect(100, hudY + 33, 260, 24, 8, 8)

        g.setColor(new Color(210,40,40))

        int hpBarWidth =
                (int)((playerHP / (double) maxHP) * 260)

        g.fillRoundRect(
                100,
                hudY + 33,
                Math.max(0, hpBarWidth),
                24,
                8,
                8
        )

        g.setColor(Color.WHITE)
        g.drawString(playerHP + " / " + maxHP, 195, hudY + 50)

        // MP
        g.setColor(new Color(60,150,255))
        g.drawString("MP", 400, hudY + 50)

        g.setColor(new Color(45,45,45))
        g.fillRoundRect(440, hudY + 33, 260, 24, 8, 8)

        g.setColor(new Color(30,110,230))

        int mpBarWidth =
                (int)((playerMana / (double) maxMana) * 260)

        g.fillRoundRect(
                440,
                hudY + 33,
                Math.max(0, mpBarWidth),
                24,
                8,
                8
        )

        g.setColor(Color.WHITE)
        g.drawString(playerMana + " / " + maxMana, 535, hudY + 50)

        g.setColor(Color.ORANGE)
        g.drawString("VIDAS:", 730, hudY + 50)

        g.setColor(new Color(255,50,100))

        String heartsText = ""

        for (int l = 0; l < playerLives; l++) {
            heartsText += "<3 "
        }

        g.drawString(heartsText, 810, hudY + 50)

        g.setColor(Color.LIGHT_GRAY)
        g.drawString(
                battleMessage + " | M = BAÃƒÅ¡ DE ITENS",
                WIDTH / 2 - 240,
                hudY + 95
        )
    }
}
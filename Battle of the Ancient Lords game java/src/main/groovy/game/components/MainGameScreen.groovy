package game.components

import game.Main
import de.gurkenlabs.litiengine.gui.screens.Screen

import java.awt.Graphics2D
import java.awt.RenderingHints

class MainGameScreen extends Screen {
    MainGameScreen() {
        super('inicio')
    }

    @Override
    void render(final Graphics2D g) {
        super.render(g)

        Main.animationTick++

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        )

        if (Main.gameState == Main.LOGIN) {
            return
        }

        if (Main.gameState == Main.MENU) {
            Main.renderMenu(g)
        } else if (Main.gameState == Main.CHARACTER_SELECT) {
            Main.renderCharacterSelect(g)
        } else if (Main.gameState == Main.GAME_OVER) {
            Main.renderGameOver(g)
        } else if (Main.gameState == Main.WIN) {
            Main.renderWin(g)
        } else if (Main.gameState == Main.INVENTORY) {
            Main.renderInventory(g)
        } else if (Main.gameState == Main.BATTLE) {
            Main.updatePositions()
            Main.renderBattle(g)
        }
    }
}

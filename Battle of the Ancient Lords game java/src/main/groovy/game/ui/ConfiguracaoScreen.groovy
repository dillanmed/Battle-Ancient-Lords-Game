package game.ui

import de.gurkenlabs.litiengine.Game
import de.gurkenlabs.litiengine.gui.screens.Screen
import de.gurkenlabs.litiengine.input.Input

import java.awt.Color
import java.awt.Cursor
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Rectangle

class ConfiguracaoScreen extends Screen {

    private Rectangle voltarRect

    ConfiguracaoScreen() {

        super("config")

        setupInput()
    }

    private void setupInput() {

        Input.mouse().onMoved { event ->

            if (!isCurrentScreen()) {
                return
            }

            boolean overButton =
                    voltarRect?.contains(event.point)

            Game.window().renderComponent.cursor =
                    overButton
                            ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                            : Cursor.getDefaultCursor()
        }

        Input.mouse().onClicked { event ->

            if (!isCurrentScreen()) {
                return
            }

            if (voltarRect?.contains(event.point)) {

                Game.screens().display("menu")
            }
        }
    }

    private boolean isCurrentScreen() {

        return Game.screens().current()?.name == name
    }

    @Override
    void render(Graphics2D g) {

        super.render(g)

        int width = Game.window().width as int
        int height = Game.window().height as int

        // FUNDO
        g.setColor(new Color(15, 15, 25))
        g.fillRect(0, 0, width, height)

        // TITULO
        g.setFont(new Font("Serif", Font.BOLD, 42))
        g.setColor(new Color(255, 215, 120))

        g.drawString(
                "CONFIGURACOES",
                width / 2 - 190,
                120
        )

        // TEXTO
        g.setFont(new Font("Arial", Font.PLAIN, 24))
        g.setColor(Color.WHITE)

        g.drawString(
                "Sistema de configuracoes em desenvolvimento...",
                width / 2 - 260,
                250
        )

        // BOTAO VOLTAR
        voltarRect = new Rectangle(
                width / 2 - 120,
                500,
                240,
                60
        )

        g.setColor(new Color(180, 60, 60))

        g.fillRoundRect(
                voltarRect.@x,
                voltarRect.@y,
                voltarRect.@width,
                voltarRect.@height,
                15,
                15
        )

        g.setColor(Color.WHITE)

        g.setFont(new Font("Arial", Font.BOLD, 24))

        g.drawString(
                "VOLTAR",
                voltarRect.@x + 65,
                voltarRect.@y + 38
        )
    }
}

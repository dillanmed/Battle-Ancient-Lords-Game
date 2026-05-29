package game.auth

import de.gurkenlabs.litiengine.Game
import de.gurkenlabs.litiengine.gui.screens.Screen
import de.gurkenlabs.litiengine.input.Input
import game.services.ServiceRegistry

import java.awt.Color
import java.awt.Cursor
import java.awt.Font
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.event.KeyEvent

class EsqueceuSenha extends Screen {
    private String email = ''
    private String mensagem = ''
    private Rectangle emailRect
    private Rectangle confirmarRect
    private Rectangle voltarRect
    private boolean typingEmail = false

    EsqueceuSenha() {
        super('esqueceuSenha')
        setupInput()
    }

    private void setupInput() {
        Input.keyboard().onKeyTyped { event ->
            if (!isCurrentScreen()) {
                return
            }

            char c = event.keyChar
            if (!Character.isISOControl(c)) {
                email += c
            }
        }

        Input.keyboard().onKeyReleased { event ->
            if (!isCurrentScreen()) {
                return
            }

            if (event.keyCode == KeyEvent.VK_BACK_SPACE && email.length() > 0) {
                email = email.substring(0, email.length() - 1)
            }
        }

        Input.mouse().onMoved { event ->
            if (!isCurrentScreen()) {
                return
            }

            boolean overAction = emailRect?.contains(event.point) || confirmarRect?.contains(event.point) || voltarRect?.contains(event.point)
            Game.window().renderComponent.cursor = overAction ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.defaultCursor
        }

        Input.mouse().onClicked { event ->
            if (!isCurrentScreen()) {
                return
            }

            if (emailRect?.contains(event.point)) {
                typingEmail = true
                return
            }

            if (voltarRect?.contains(event.point)) {
                typingEmail = false
                Game.screens().display('login')
                return
            }

            if (confirmarRect?.contains(event.point)) {
                ServiceRegistry.authService.solicitarRecuperacaoSenha(email)
                mensagem = 'Se o e-mail existir, enviaremos as instrucoes.'
            }
        }
    }

    private boolean isCurrentScreen() {
        Game.screens().current()?.name == name
    }

    @Override
    void render(Graphics2D g) {
        super.render(g)

        int width = Game.window().width
        int height = Game.window().height
        int panelWidth = Math.min(620, (int) (width * 0.48))
        int x = (width - panelWidth) / 2
        int y = (int) (height * 0.28)

        g.color = new Color(21, 20, 30)
        g.fillRect(0, 0, width, height)

        g.font = new Font('Serif', Font.BOLD, 48)
        drawCentered(g, 'Recuperar senha', width, y - 60)

        emailRect = new Rectangle(x, y, panelWidth, 64)
        g.color = new Color(40, 30, 22, 230)
        g.fillRoundRect(emailRect.x, emailRect.y, emailRect.width, emailRect.height, 12, 12)
        g.color = new Color(180, 120, 60)
        g.drawRoundRect(emailRect.x, emailRect.y, emailRect.width, emailRect.height, 12, 12)

        g.font = new Font('Arial', Font.BOLD, 20)
        g.color = Color.WHITE
        g.drawString(email ?: 'E-mail', x + 24, y + 40)
        if (typingEmail) {
            g.drawString('|', x + 28 + g.fontMetrics.stringWidth(email), y + 40)
        }

        confirmarRect = new Rectangle(x, y + 95, panelWidth, 58)
        g.color = new Color(105, 73, 32)
        g.fillRoundRect(confirmarRect.x, confirmarRect.y, confirmarRect.width, confirmarRect.height, 12, 12)
        g.color = Color.WHITE
        drawCentered(g, 'Confirmar', width, confirmarRect.y + 38)

        voltarRect = new Rectangle(50, 45, 120, 40)
        g.drawString('< Voltar', voltarRect.x, voltarRect.y + 28)

        if (mensagem) {
            drawCentered(g, mensagem, width, confirmarRect.y + 95)
        }
    }

    private void drawCentered(Graphics2D g, String text, int screenWidth, int y) {
        int x = (screenWidth - g.fontMetrics.stringWidth(text)) / 2
        g.drawString(text, x, y)
    }
}

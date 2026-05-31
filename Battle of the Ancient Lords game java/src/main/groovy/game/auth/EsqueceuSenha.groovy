package game.auth

import de.gurkenlabs.litiengine.Game
import de.gurkenlabs.litiengine.gui.screens.Screen
import de.gurkenlabs.litiengine.input.Input
import game.services.ServiceRegistry

import javax.imageio.ImageIO
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Cursor
import java.awt.Font
import java.awt.FontMetrics
import java.awt.GraphicsEnvironment
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import java.awt.Image
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage

class EsqueceuSenha extends Screen {
    private BufferedImage background
    private BufferedImage confirmarButtonImage
    private Font medievalFont

    private String email = ''
    private boolean emailSelecionado = false

    private String mensagemPopup = ''
    private boolean mostrarPopup = false
    private boolean popupSucesso = false
    private long tempoPopup = 0

    private Rectangle emailRect
    private Rectangle confirmarRect
    private Rectangle voltarRect

    EsqueceuSenha() {

        super('esqueceuSenha')
        loadAssets()
        setupKeyboardInput()
        setupMouseInput()
    }

    @Override
    void prepare() {
        super.prepare()
        resetarTela()
    }

    private void resetarTela() {
        email = ''
        emailSelecionado = false
        mensagemPopup = ''
        mostrarPopup = false
        popupSucesso = false
        tempoPopup = 0
    }

    private void loadAssets() {
        try {
            background = loadImage('/assets/esqueceuSenha/fundo_tela_esqueceusenha.png')
            confirmarButtonImage = loadImage('/assets/configuracoes/btn_confirmar.png')
            medievalFont = Font.createFont(Font.TRUETYPE_FONT, resource('/assets/fontes/Cinzel-Bold.ttf')).deriveFont(30f)
            GraphicsEnvironment.localGraphicsEnvironment.registerFont(medievalFont)
        } catch (Exception exception) {
            println "Erro ao carregar assets de recuperacao de senha: ${exception.message}"
            medievalFont = new Font('Serif', Font.BOLD, 30)
        }
    }

    private BufferedImage loadImage(String path) {
        ImageIO.read(resource(path))
    }

    private InputStream resource(String path) {
        InputStream stream = getClass().getResourceAsStream(path)
        if (stream == null) {
            throw new FileNotFoundException(path)
        }
        stream
    }

    private void setupKeyboardInput() {
        Input.keyboard().onKeyTyped { event ->

            if (!isCurrentScreen()) {
                return
            }

            if (!emailSelecionado) {
                return
            }

            char c = event.keyChar
            if (c == KeyEvent.CHAR_UNDEFINED || Character.isISOControl(c)) {
                return
            }

            if (!Character.isLetterOrDigit(c) && '@._-'.indexOf(c) == -1) {
                return
            }

            if (email.length() >= 28) {
                return
            }

            email += c
        }

        Input.keyboard().onKeyReleased { event ->

            if (!isCurrentScreen()) {
                return
            }

            if (event.keyCode == KeyEvent.VK_BACK_SPACE && emailSelecionado && email.length() > 0) {
                email = email.substring(0, email.length() - 1)
            }
        }
    }

    private void setupMouseInput() {
        Input.mouse().onMoved { event ->
            if (isCurrentScreen()) {
                updateCursor(event.point)
            }
        }

        Input.mouse().onClicked { event ->

            if (!isCurrentScreen()) {
                return
            }

            Point mousePosition = event.point

            if (voltarRect?.contains(mousePosition)) {
                emailSelecionado = false
                Game.screens().display('login')

                return
            }

            emailSelecionado = emailRect?.contains(mousePosition) ?: false

            if (confirmarRect?.contains(mousePosition)) {
                validarEmail()
            }
        }
    }

    private boolean isCurrentScreen() {

        return Game.screens().current()?.name == name
    }

    private void validarEmail() {
        if (!emailValido(email)) {
            mostrarPopup('Digite um e-mail valido.', false)
            return
        }

        try {
            String mensagem = ServiceRegistry.authService.solicitarRecuperacaoSenha(email)
            mostrarPopup(mensagem ?: 'E-mail de recuperacao enviado.', true)
        } catch (Exception exception) {
            mostrarPopup(exception.message ?: 'Nao foi possivel enviar.', false)
        }
    }

    private boolean emailValido(String valor) {
        String emailTratado = valor?.trim() ?: ''

        emailTratado.contains('@') &&
                emailTratado.contains('.') &&
                emailTratado.indexOf('@') > 0 &&
                emailTratado.lastIndexOf('.') > emailTratado.indexOf('@') + 1 &&
                emailTratado.lastIndexOf('.') < emailTratado.length() - 1
    }

    private void mostrarPopup(String mensagem, boolean sucesso) {
        mensagemPopup = mensagem
        popupSucesso = sucesso
        mostrarPopup = true
        tempoPopup = System.currentTimeMillis()
    }

    private void updateCursor(Point mousePosition) {
        if (emailRect?.contains(mousePosition)) {
            Game.window().renderComponent.cursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR)
            return
        }

        if (confirmarRect?.contains(mousePosition) || voltarRect?.contains(mousePosition)) {
            Game.window().renderComponent.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            return
        }

        Game.window().renderComponent.cursor = Cursor.defaultCursor
    }

    @Override
    void render(Graphics2D g) {

        super.render(g)

        int screenWidth = Game.window().width
        int screenHeight = Game.window().height

        renderBackground(g, screenWidth, screenHeight)
        renderBotaoVoltar(g)

        g.font = medievalFont.deriveFont(34f)
        String titulo = 'Digite seu e-mail para recuperar sua senha'
        drawOutlinedText(g, titulo, centeredTextX(g, titulo, screenWidth.intdiv(2)), scaledY(screenHeight, 240), Color.WHITE)

        int inputWidth = Math.min(620, Math.max(320, (int) (screenWidth * 0.48)))
        int inputHeight = 65
        int inputX = screenWidth.intdiv(2) - inputWidth.intdiv(2)
        int inputY = scaledY(screenHeight, 330)

        emailRect = new Rectangle(inputX, inputY, inputWidth, inputHeight)
        drawInputBox(g, emailRect, email, emailSelecionado)

        int confirmarWidth = 250
        int confirmarHeight = 85
        int confirmarX = screenWidth.intdiv(2) - confirmarWidth.intdiv(2)
        int confirmarY = scaledY(screenHeight, 470)

        if (confirmarButtonImage != null) {
            g.drawImage(confirmarButtonImage, confirmarX, confirmarY, confirmarWidth, confirmarHeight, null)
        } else {
            drawFallbackButton(g, confirmarX, confirmarY, confirmarWidth, confirmarHeight)
        }

        confirmarRect = new Rectangle(confirmarX, confirmarY, confirmarWidth, confirmarHeight)
        renderPopup(g, screenWidth, screenHeight)
    }

    private void renderBackground(Graphics2D g, int screenWidth, int screenHeight) {
        if (background != null) {
            g.drawImage(background, 0, 0, screenWidth, screenHeight, null)
            return
        }

        g.color = new Color(21, 20, 30)
        g.fillRect(0, 0, screenWidth, screenHeight)
    }

    private void renderBotaoVoltar(Graphics2D g) {
        g.font = medievalFont.deriveFont(22f)
        String texto = '< Voltar'
        FontMetrics metrics = g.fontMetrics
        int x = 55
        int y = 75

        drawOutlinedText(g, texto, x, y, Color.WHITE)
        voltarRect = new Rectangle(x - 10, y - 30, metrics.stringWidth(texto) + 20, 40)
    }

    private void drawInputBox(Graphics2D g, Rectangle rect, String texto, boolean selecionado) {
        int rectX = rect.@x
        int rectY = rect.@y
        int rectWidth = rect.@width
        int rectHeight = rect.@height

        g.color = new Color(35, 25, 15, 220)
        g.fillRoundRect(rectX, rectY, rectWidth, rectHeight, 18, 18)

        g.color = selecionado ? new Color(210, 180, 90) : new Color(130, 100, 50)
        g.stroke = new BasicStroke(3)
        g.drawRoundRect(rectX, rectY, rectWidth, rectHeight, 18, 18)

        g.font = new Font('Arial', Font.BOLD, 20)
        FontMetrics metrics = g.fontMetrics
        String textoExibido = texto ?: 'E-mail'
        int textX = rectX + 20
        int textY = rectY + (rectHeight - metrics.height).intdiv(2) + metrics.ascent

        drawOutlinedText(g, textoExibido, textX, textY, Color.WHITE)

        if (selecionado) {
            int cursorX = textX + metrics.stringWidth(textoExibido) + 2
            drawOutlinedText(g, '|', cursorX, textY, Color.WHITE)
        }
    }

    private void drawFallbackButton(Graphics2D g, int x, int y, int width, int height) {
        g.color = new Color(105, 73, 32)
        g.fillRoundRect(x, y, width, height, 12, 12)
        g.color = Color.WHITE
        g.font = medievalFont.deriveFont(22f)
        drawOutlinedText(g, 'Confirmar', centeredTextX(g, 'Confirmar', x + width.intdiv(2)), y + 52, Color.WHITE)
    }

    private void renderPopup(Graphics2D g, int screenWidth, int screenHeight) {
        if (!mostrarPopup) {
            return
        }

        if (System.currentTimeMillis() - tempoPopup > 2000) {
            mostrarPopup = false
            return
        }

        g.font = new Font('Arial', Font.BOLD, 18)
        FontMetrics metrics = g.fontMetrics
        int x = (screenWidth - metrics.stringWidth(mensagemPopup)).intdiv(2)
        int y = scaledY(screenHeight, 445)

        g.color = popupSucesso ? new Color(40, 120, 40, 220) : new Color(120, 40, 40, 220)
        g.fillRoundRect(x - 20, y - 28, metrics.stringWidth(mensagemPopup) + 40, 40, 15, 15)

        g.color = popupSucesso ? new Color(180, 220, 180) : new Color(220, 160, 160)
        g.stroke = new BasicStroke(2)
        g.drawRoundRect(x - 20, y - 28, metrics.stringWidth(mensagemPopup) + 40, 40, 15, 15)
        drawOutlinedText(g, mensagemPopup, x, y, Color.WHITE)
    }

    private int centeredTextX(Graphics2D g, String text, Number centerX) {
        centerX.intValue() - g.fontMetrics.stringWidth(text).intdiv(2)
    }

    private int scaledY(int screenHeight, int baseY) {
        Math.round(baseY * (screenHeight / 720f))
    }

    private void drawOutlinedText(Graphics2D g, String text, int x, int y, Color mainColor) {
        g.color = Color.BLACK
        g.drawString(text, x - 1, y - 1)
        g.drawString(text, x + 1, y - 1)
        g.drawString(text, x - 1, y + 1)
        g.drawString(text, x + 1, y + 1)
        g.color = mainColor
        g.drawString(text, x, y)
    }
}
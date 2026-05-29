package game.ui

import game.Main
import game.services.ServiceRegistry
import de.gurkenlabs.litiengine.Game
import de.gurkenlabs.litiengine.gui.screens.Screen
import de.gurkenlabs.litiengine.input.Input

import javax.imageio.ImageIO
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Cursor
import java.awt.Font
import java.awt.FontMetrics
import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage

class LoginScreen extends Screen {

    private BufferedImage background
    private BufferedImage usuarioInput
    private BufferedImage senhaInput
    private BufferedImage confirmarButton
    private BufferedImage cadastrarButton
    private BufferedImage esqueceuSenhaButton
    private BufferedImage logoImage

    private String username = ''
    private String password = ''

    private String mensagemPopup = ''
    private boolean mostrarPopup = false
    private boolean popupSucesso = false
    private long tempoPopup = 0

    private Font pixelFont

    private long lastBlinkTime = 0
    private boolean showCursor = true
    private boolean typingUsername = false
    private boolean typingPassword = false

    private Rectangle cadastrarRect
    private Rectangle esqueceuSenhaRect
    private Rectangle confirmarRect
    private Rectangle usuarioRect
    private Rectangle senhaRect

    private int screenWidth
    private int screenHeight
    private int inputWidth
    private int inputHeight
    private int inputX
    private int confirmWidth
    private int confirmHeight
    private int confirmX
    private int smallButtonWidth
    private int smallButtonHeight
    private int esqueceuSenhaX
    private int cadastrarX
    private int startY
    private int senhaY
    private int esqueceuY
    private int confirmarY

    LoginScreen() {
        super('login')
        loadImages()
        setupKeyboardInput()
        setupMouseInput()
    }

    @Override
    void prepare() {
        super.prepare()
        resetarTela()
    }

    private void resetarTela() {
        username = ''
        password = ''
        mensagemPopup = ''
        mostrarPopup = false
        popupSucesso = false
        tempoPopup = 0
        typingUsername = false
        typingPassword = false
        showCursor = true
        lastBlinkTime = System.currentTimeMillis()
    }

    private void loadImages() {
        try {
            background = loadImage('/assets/login/fundo_tela_login.png')
            usuarioInput = loadImage('/assets/login/ct_usuario.png')
            senhaInput = loadImage('/assets/login/ct_senha.png')
            confirmarButton = loadImage('/assets/login/btn_confirmar.png')
            cadastrarButton = loadImage('/assets/login/btn_cadastro.png')
            esqueceuSenhaButton = loadImage('/assets/login/btn_esqueceusenha.png')
            logoImage = loadImage('/assets/login/logo.png')
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, resource('/assets/fontes/Pixel.ttf'))
        } catch (Exception e) {
            println "Erro ao carregar imagens do login: ${e.message}"
            pixelFont = new Font('Serif', Font.BOLD, 72)
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

            char c = event.keyChar
            if (Character.isISOControl(c)) {
                return
            }

            if (typingUsername) {
                username += c
            } else if (typingPassword) {
                password += c
            }
        }

        Input.keyboard().onKeyReleased { event ->
            if (!isCurrentScreen()) {
                return
            }

            if (event.keyCode == KeyEvent.VK_BACK_SPACE) {
                apagarUltimoCaractere()
            }

            if (event.keyCode == KeyEvent.VK_TAB) {
                alternarCampo()
            }
        }
    }

    private boolean isCurrentScreen() {
        Game.screens().current()?.name == name
    }

    private void apagarUltimoCaractere() {
        if (typingUsername && username.length() > 0) {
            username = username.substring(0, username.length() - 1)
        } else if (typingPassword && password.length() > 0) {
            password = password.substring(0, password.length() - 1)
        }
    }

    private void alternarCampo() {
        typingUsername = !typingUsername
        typingPassword = !typingPassword
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

            if (usuarioRect?.contains(mousePosition)) {
                typingUsername = true
                typingPassword = false
            }

            if (senhaRect?.contains(mousePosition)) {
                typingUsername = false
                typingPassword = true
            }

            if (esqueceuSenhaRect?.contains(mousePosition)) {
                Game.screens().display('esqueceuSenha')
            }

            if (cadastrarRect?.contains(mousePosition)) {
                Game.screens().display('cadastro')
            }

            if (confirmarRect?.contains(mousePosition)) {
                validarLogin()
            }
        }
    }

    private void updateCursor(Point mousePosition) {
        if (usuarioRect?.contains(mousePosition) || senhaRect?.contains(mousePosition)) {
            Game.window().renderComponent.cursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR)
            return
        }

        if (confirmarRect?.contains(mousePosition) ||
                cadastrarRect?.contains(mousePosition) ||
                esqueceuSenhaRect?.contains(mousePosition)) {
            Game.window().renderComponent.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            return
        }

        Game.window().renderComponent.cursor = Cursor.defaultCursor
    }

    private void validarLogin() {
        if (ServiceRegistry.authService.login(username, password) != null) {
            Main.gameState = Main.MENU
            Game.screens().display('inicio')
        } else {
            mostrarPopup('Usuario ou senha invalidos', false)
        }
    }

    private void mostrarPopup(String mensagem, boolean sucesso) {
        mensagemPopup = mensagem
        popupSucesso = sucesso
        mostrarPopup = true
        tempoPopup = System.currentTimeMillis()
    }

    @Override
    void render(Graphics2D g) {
        super.render(g)

        if (System.currentTimeMillis() - lastBlinkTime > 500) {
            showCursor = !showCursor
            lastBlinkTime = System.currentTimeMillis()
        }

        calcularMedidas()
        renderBackground(g)
        renderLogo(g)
        renderCampoUsuario(g)
        renderCampoSenha(g)
        renderEsqueceuSenha(g)
        renderBotaoConfirmar(g)
        renderPopup(g)
        renderCadastrar(g)
    }

    private void calcularMedidas() {
        screenWidth = Game.window().width
        screenHeight = Game.window().height
        inputWidth = (int) (screenWidth * 0.34)
        inputHeight = (int) (inputWidth * 0.22)
        inputX = (screenWidth - inputWidth) / 2
        confirmWidth = (int) (inputWidth * 0.72)
        confirmHeight = (int) (confirmWidth * 220 / 700)
        confirmX = (screenWidth - confirmWidth) / 2
        smallButtonWidth = (int) (inputWidth * 0.45)
        smallButtonHeight = (int) (smallButtonWidth * 140 / 500)
        esqueceuSenhaX = inputX
        cadastrarX = inputX + inputWidth - smallButtonWidth

        int spacing = (int) (screenHeight * 0.025)
        startY = (int) (screenHeight * 0.24)
        senhaY = startY + inputHeight + spacing
        esqueceuY = senhaY + inputHeight + spacing
        confirmarY = esqueceuY + smallButtonHeight + (spacing / 2)
    }

    private void renderBackground(Graphics2D g) {
        if (background != null) {
            g.drawImage(background, 0, 0, screenWidth, screenHeight, null)
            return
        }

        g.color = new Color(21, 20, 30)
        g.fillRect(0, 0, screenWidth, screenHeight)
    }

    private void renderLogo(Graphics2D g) {
        if (logoImage != null) {
            int logoWidth = (int) (screenWidth * 0.32)
            int logoHeight = (int) (logoWidth * 250 / 700)
            int x = (screenWidth - logoWidth) / 2
            int y = (int) (screenHeight * 0.035)
            g.drawImage(logoImage, x, y, logoWidth, logoHeight, null)
            return
        }

        g.font = pixelFont.deriveFont(72f)
        String titulo = 'Ancient Lords'
        int x = centralizarTextoX(g, titulo, 0, screenWidth)
        int y = (int) (screenHeight * 0.16)
        drawOutlinedText(g, titulo, x, y, Color.WHITE)
    }

    private void renderCampoUsuario(Graphics2D g) {
        drawImageOrFallback(g, usuarioInput, inputX, startY, inputWidth, inputHeight)
        usuarioRect = new Rectangle(inputX, startY, inputWidth, inputHeight)

        g.font = new Font('Arial', Font.BOLD, 20)
        String displayedUsername = limitarTexto(g, username, inputWidth - 180)
        int textX = inputX + 190
        int textY = startY + (inputHeight / 2) + 8
        drawOutlinedText(g, displayedUsername, textX, textY, Color.WHITE)

        if (typingUsername && showCursor) {
            int cursorX = textX + g.fontMetrics.stringWidth(displayedUsername) + 3
            drawOutlinedText(g, '|', cursorX, textY, Color.WHITE)
        }
    }

    private void renderCampoSenha(Graphics2D g) {
        drawImageOrFallback(g, senhaInput, inputX, senhaY, inputWidth, inputHeight)
        senhaRect = new Rectangle(inputX, senhaY, inputWidth, inputHeight)

        g.font = new Font('Arial', Font.BOLD, 20)
        String hiddenPassword = '*' * password.length()
        String displayedPassword = limitarTexto(g, hiddenPassword, inputWidth - 180)
        int textX = inputX + 190
        int textY = senhaY + (inputHeight / 2) + 8
        drawOutlinedText(g, displayedPassword, textX, textY, Color.WHITE)

        if (typingPassword && showCursor) {
            int cursorX = textX + g.fontMetrics.stringWidth(displayedPassword) + 3
            drawOutlinedText(g, '|', cursorX, textY, Color.WHITE)
        }
    }

    private void renderEsqueceuSenha(Graphics2D g) {
        drawImageOrFallback(g, esqueceuSenhaButton, esqueceuSenhaX, esqueceuY, smallButtonWidth, smallButtonHeight)
        esqueceuSenhaRect = new Rectangle(esqueceuSenhaX, esqueceuY, smallButtonWidth, smallButtonHeight)
    }

    private void renderBotaoConfirmar(Graphics2D g) {
        drawImageOrFallback(g, confirmarButton, confirmX, confirmarY, confirmWidth, confirmHeight)
        confirmarRect = new Rectangle(confirmX, confirmarY, confirmWidth, confirmHeight)
    }

    private void renderPopup(Graphics2D g) {
        if (!mostrarPopup) {
            return
        }

        if (System.currentTimeMillis() - tempoPopup > 2000) {
            mostrarPopup = false
            return
        }

        g.font = new Font('Arial', Font.BOLD, 18)
        FontMetrics metrics = g.fontMetrics
        int x = (screenWidth - metrics.stringWidth(mensagemPopup)) / 2
        int y = confirmarY + confirmHeight + 30

        g.color = popupSucesso ? new Color(40, 120, 40, 220) : new Color(120, 40, 40, 220)
        g.fillRoundRect(x - 20, y - 28, metrics.stringWidth(mensagemPopup) + 40, 40, 15, 15)
        g.color = popupSucesso ? new Color(180, 220, 180) : new Color(220, 160, 160)
        g.stroke = new BasicStroke(2)
        g.drawRoundRect(x - 20, y - 28, metrics.stringWidth(mensagemPopup) + 40, 40, 15, 15)
        drawOutlinedText(g, mensagemPopup, x, y, Color.WHITE)
    }

    private void renderCadastrar(Graphics2D g) {
        g.font = new Font('Arial', Font.PLAIN, 20)
        drawImageOrFallback(g, cadastrarButton, cadastrarX, esqueceuY, smallButtonWidth, smallButtonHeight)
        cadastrarRect = new Rectangle(cadastrarX, esqueceuY, smallButtonWidth, smallButtonHeight)
    }

    private void drawImageOrFallback(Graphics2D g, BufferedImage image, int x, int y, int width, int height) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null)
            return
        }

        g.color = new Color(40, 30, 22, 230)
        g.fillRoundRect(x, y, width, height, 12, 12)
        g.color = new Color(180, 120, 60)
        g.stroke = new BasicStroke(2)
        g.drawRoundRect(x, y, width, height, 12, 12)
    }

    private String limitarTexto(Graphics2D g, String texto, int larguraMaxima) {
        while (texto && g.fontMetrics.stringWidth(texto) > larguraMaxima) {
            texto = texto.substring(1)
        }
        texto
    }

    private int centralizarTextoX(Graphics2D g, String texto, int x, int width) {
        x + (width / 2) - (g.fontMetrics.stringWidth(texto) / 2)
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

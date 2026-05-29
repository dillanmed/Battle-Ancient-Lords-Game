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

class CadastroScreen extends Screen {

    private BufferedImage background
    private BufferedImage usuarioField
    private BufferedImage emailField
    private BufferedImage senhaField
    private BufferedImage confirmarButton

    private String username = ''
    private String email = ''
    private String password = ''

    private String mensagemPopup = ''
    private boolean mostrarPopup = false
    private boolean popupSucesso = false
    private long tempoPopup = 0

    private Font medievalFont
    private Font pixelFont

    private boolean typingUsername = false
    private boolean typingEmail = false
    private boolean typingPassword = false

    private long lastBlinkTime = 0
    private boolean showCursor = true

    private Rectangle usernameRect
    private Rectangle emailRect
    private Rectangle passwordRect
    private Rectangle confirmarRect
    private Rectangle voltarRect

    private int screenWidth
    private int screenHeight
    private int fieldWidth
    private int fieldHeight
    private int fieldX
    private int buttonWidth
    private int buttonHeight
    private int buttonX
    private int inputPaddingLeft
    private int startY
    private int emailY
    private int senhaY
    private int confirmarY

    CadastroScreen() {
        super('cadastro')
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
        email = ''
        password = ''
        mensagemPopup = ''
        mostrarPopup = false
        popupSucesso = false
        tempoPopup = 0
        typingUsername = false
        typingEmail = false
        typingPassword = false
        showCursor = true
        lastBlinkTime = System.currentTimeMillis()
    }

    private void loadImages() {
        try {
            background = loadImage('/assets/cadastro/fundo_tela_cadastro.png')
            usuarioField = loadImage('/assets/cadastro/ct_usuario.png')
            emailField = loadImage('/assets/cadastro/ct_email.png')
            senhaField = loadImage('/assets/cadastro/ct_senha.png')
            confirmarButton = loadImage('/assets/cadastro/btn_confirmar.png')
            medievalFont = Font.createFont(Font.TRUETYPE_FONT, resource('/assets/fontes/Cinzel-Bold.ttf'))
            pixelFont = Font.createFont(Font.TRUETYPE_FONT, resource('/assets/fontes/Pixel.ttf'))
        } catch (Exception e) {
            println "Erro ao carregar imagens do cadastro: ${e.message}"
            medievalFont = new Font('Serif', Font.BOLD, 20)
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
            if (c == KeyEvent.CHAR_UNDEFINED || Character.isISOControl(c)) {
                return
            }

            if (typingUsername) {
                username += c
            } else if (typingEmail) {
                email += c
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
        } else if (typingEmail && email.length() > 0) {
            email = email.substring(0, email.length() - 1)
        } else if (typingPassword && password.length() > 0) {
            password = password.substring(0, password.length() - 1)
        }
    }

    private void alternarCampo() {
        if (typingUsername) {
            selecionarCampoEmail()
        } else if (typingEmail) {
            selecionarCampoSenha()
        } else {
            selecionarCampoUsuario()
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
                limparSelecaoCampos()
                Game.screens().display('login')
                return
            }

            if (usernameRect?.contains(mousePosition)) {
                selecionarCampoUsuario()
            } else if (emailRect?.contains(mousePosition)) {
                selecionarCampoEmail()
            } else if (passwordRect?.contains(mousePosition)) {
                selecionarCampoSenha()
            } else {
                limparSelecaoCampos()
            }

            if (confirmarRect?.contains(mousePosition)) {
                validarCadastro()
            }
        }
    }

    private void updateCursor(Point mousePosition) {
        if (usernameRect?.contains(mousePosition) ||
                emailRect?.contains(mousePosition) ||
                passwordRect?.contains(mousePosition)) {
            Game.window().renderComponent.cursor = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR)
            return
        }

        if (confirmarRect?.contains(mousePosition) || voltarRect?.contains(mousePosition)) {
            Game.window().renderComponent.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            return
        }

        Game.window().renderComponent.cursor = Cursor.defaultCursor
    }

    private void validarCadastro() {
        if (username.trim().empty || email.trim().empty || password.trim().empty) {
            mostrarPopup('Preencha todos os campos', false)
            return
        }

        if (!emailValido(email)) {
            mostrarPopup('Digite um e-mail valido', false)
            return
        }

        ServiceRegistry.authService.cadastrar(username, email, password)
        Main.gameState = Main.MENU
        Game.screens().display('inicio')
    }

    private boolean emailValido(String email) {
        String emailTratado = email.trim()

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

    private void selecionarCampoUsuario() {
        typingUsername = true
        typingEmail = false
        typingPassword = false
    }

    private void selecionarCampoEmail() {
        typingUsername = false
        typingEmail = true
        typingPassword = false
    }

    private void selecionarCampoSenha() {
        typingUsername = false
        typingEmail = false
        typingPassword = true
    }

    private void limparSelecaoCampos() {
        typingUsername = false
        typingEmail = false
        typingPassword = false
    }

    @Override
    void render(Graphics2D g) {
        super.render(g)

        atualizarCursor()
        calcularMedidas()
        renderBackground(g)
        renderTituloTela(g)
        renderCampoUsuario(g)
        renderCampoEmail(g)
        renderCampoSenha(g)
        renderBotaoConfirmar(g)
        renderPopup(g)
        renderBotaoVoltar(g)
    }

    private void atualizarCursor() {
        if (System.currentTimeMillis() - lastBlinkTime > 500) {
            showCursor = !showCursor
            lastBlinkTime = System.currentTimeMillis()
        }
    }

    private void calcularMedidas() {
        screenWidth = Game.window().width
        screenHeight = Game.window().height
        fieldWidth = Math.min(620, Math.min((int) (screenWidth * 0.40), (int) (screenHeight * 0.62)))
        fieldHeight = (int) (fieldWidth * 300 / 1000)
        fieldX = (screenWidth - fieldWidth) / 2
        buttonWidth = (int) (fieldWidth * 0.68)
        buttonHeight = (int) (buttonWidth * 220 / 700)
        buttonX = (screenWidth - buttonWidth) / 2
        inputPaddingLeft = (int) (fieldWidth * 0.34)

        int spacing = Math.max(12, (int) (screenHeight * 0.021))
        startY = (int) (screenHeight * 0.22)
        emailY = startY + fieldHeight + spacing
        senhaY = startY + (fieldHeight + spacing) * 2
        confirmarY = startY + (fieldHeight + spacing) * 3 + 10
    }

    private void renderBackground(Graphics2D g) {
        if (background != null) {
            g.drawImage(background, 0, 0, screenWidth, screenHeight, null)
            return
        }

        g.color = new Color(21, 20, 30)
        g.fillRect(0, 0, screenWidth, screenHeight)
    }

    private void renderTituloTela(Graphics2D g) {
        g.font = pixelFont.deriveFont(72f)
        String titulo = 'Cadastre-se'
        int x = centralizarTextoX(g, titulo, 0, screenWidth)
        int y = (int) (screenHeight * 0.16)
        drawOutlinedText(g, titulo, x, y, Color.WHITE)
    }

    private void renderCampoUsuario(Graphics2D g) {
        renderTitulo(g, 'Usuario', startY - 10)
        renderCampoTexto(g, usuarioField, username, fieldX, startY, false, typingUsername)
        usernameRect = new Rectangle(fieldX, startY, fieldWidth, fieldHeight)
    }

    private void renderCampoEmail(Graphics2D g) {
        renderTitulo(g, 'E-mail', emailY - 4)
        renderCampoTexto(g, emailField, email, fieldX, emailY, false, typingEmail)
        emailRect = new Rectangle(fieldX, emailY, fieldWidth, fieldHeight)
    }

    private void renderCampoSenha(Graphics2D g) {
        renderTitulo(g, 'Criar senha', senhaY - 4)
        renderCampoTexto(g, senhaField, password, fieldX, senhaY, true, typingPassword)
        passwordRect = new Rectangle(fieldX, senhaY, fieldWidth, fieldHeight)
    }

    private void renderTitulo(Graphics2D g, String texto, int y) {
        g.font = medievalFont.deriveFont(20f)
        int x = centralizarTextoX(g, texto, 0, screenWidth)
        drawOutlinedText(g, texto, x, y, Color.WHITE)
    }

    private void renderCampoTexto(
            Graphics2D g,
            BufferedImage imagem,
            String texto,
            int x,
            int y,
            boolean senha,
            boolean selecionado
    ) {
        drawImageOrFallback(g, imagem, x, y, fieldWidth, fieldHeight)
        g.font = new Font('Arial', Font.BOLD, 20)

        String textoExibido = senha ? '*' * texto.length() : texto
        textoExibido = limitarTexto(g, textoExibido, fieldWidth - inputPaddingLeft - 40)

        int textX = x + inputPaddingLeft
        int textY = y + (fieldHeight / 2) + 8
        drawOutlinedText(g, textoExibido, textX, textY, Color.WHITE)

        if (selecionado && showCursor) {
            int cursorX = textX + g.fontMetrics.stringWidth(textoExibido) + 3
            drawOutlinedText(g, '|', cursorX, textY, Color.WHITE)
        }
    }

    private void renderBotaoConfirmar(Graphics2D g) {
        drawImageOrFallback(g, confirmarButton, buttonX, confirmarY, buttonWidth, buttonHeight)
        confirmarRect = new Rectangle(buttonX, confirmarY, buttonWidth, buttonHeight)
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
        int y = confirmarY + buttonHeight + 25

        g.color = popupSucesso ? new Color(40, 120, 40, 220) : new Color(120, 40, 40, 220)
        g.fillRoundRect(x - 20, y - 28, metrics.stringWidth(mensagemPopup) + 40, 40, 15, 15)
        g.color = popupSucesso ? new Color(180, 220, 180) : new Color(220, 160, 160)
        g.stroke = new BasicStroke(2)
        g.drawRoundRect(x - 20, y - 28, metrics.stringWidth(mensagemPopup) + 40, 40, 15, 15)
        drawOutlinedText(g, mensagemPopup, x, y, Color.WHITE)
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

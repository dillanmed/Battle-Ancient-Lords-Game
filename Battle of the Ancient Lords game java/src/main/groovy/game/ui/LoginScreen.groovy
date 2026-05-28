package game.ui

import game.Main
import de.gurkenlabs.litiengine.Game

import javax.imageio.ImageIO
import javax.swing.*
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage

class LoginScreen extends JPanel {

    BufferedImage backgroundImage

    JTextField loginField
    JTextField emailField
    JPasswordField senhaField

    JTextField nomeCadastroField
    JTextField emailCadastroField
    JPasswordField senhaCadastroField

    JButton btnLogin
    JButton btnCadastro

    CardLayout cardLayout
    JPanel cardsPanel

    // TAMANHO FIXO DA TELA
    final int WIDTH = 1280
    final int HEIGHT = 720

    LoginScreen() {

        setLayout(new BorderLayout())
        setFocusable(true)

        // BACKGROUND
        try {

            backgroundImage = ImageIO.read(
                    new File("src/main/resources/Background/menu.jpg")
            )

        } catch (Exception e) {

            println("Erro ao carregar background: " + e.getMessage())
        }

        cardLayout = new CardLayout()

        cardsPanel = new JPanel(cardLayout)
        cardsPanel.setOpaque(false)

        cardsPanel.add(createLoginPanel(), "LOGIN")
        cardsPanel.add(createCadastroPanel(), "CADASTRO")

        add(cardsPanel, BorderLayout.CENTER)

        cardLayout.show(cardsPanel, "LOGIN")
    }

    JPanel createLoginPanel() {

        JPanel panel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g)

                if (backgroundImage != null) {

                    g.drawImage(
                            backgroundImage,
                            0,
                            0,
                            getWidth(),
                            getHeight(),
                            null
                    )
                }
            }
        }

        panel.setLayout(null)
        panel.setOpaque(false)

        // =========================
        // CENTRALIZAÇÃO
        // =========================

        int centerX = WIDTH / 2

        int fieldWidth = 320
        int fieldHeight = 45

        int labelX = centerX - 250
        int fieldX = centerX - 80

        // TITULO
        JLabel titulo = new JLabel("LOGIN")

        titulo.setBounds(centerX - 100, 70, 250, 50)
        titulo.setFont(new Font("Arial", Font.BOLD, 46))
        titulo.setForeground(Color.WHITE)

        // LOGIN
        JLabel loginLabel = createLabel("Login", 220, labelX)

        loginField = new JTextField()
        loginField.setBounds(fieldX, 220, fieldWidth, fieldHeight)

        styleField(loginField)

        // EMAIL
        JLabel emailLabel = createLabel("Email", 300, labelX)

        emailField = new JTextField()
        emailField.setBounds(fieldX, 300, fieldWidth, fieldHeight)

        styleField(emailField)

        // SENHA
        JLabel senhaLabel = createLabel("Senha", 380, labelX)

        senhaField = new JPasswordField()
        senhaField.setBounds(fieldX, 380, fieldWidth, fieldHeight)

        styleField(senhaField)

        // BOTAO LOGIN
        btnLogin = new JButton("ENTRAR")

        btnLogin.setBounds(centerX - 110, 480, 220, 50)

        styleButton(btnLogin)

        btnLogin.addActionListener {

            String login = loginField.getText()
            String email = emailField.getText()
            String senha = new String(senhaField.getPassword())

            if (!login.isEmpty()
                    && !email.isEmpty()
                    && !senha.isEmpty()) {

                println("[LOGIN] Sucesso")

                Main.gameState = Main.MENU

                // VOLTA O RENDER DO JOGO
                Game.window().getHostControl().setContentPane(
                        Game.window().getRenderComponent().getParent()
                )

                Game.window().getRenderComponent().requestFocus()

            } else {

                JOptionPane.showMessageDialog(
                        null,
                        "Preencha todos os campos!"
                )
            }
        }

        // CRIAR CONTA
        JButton irCadastro = new JButton("CRIAR CONTA")

        irCadastro.setBounds(centerX - 110, 550, 220, 45)

        styleButton(irCadastro)

        irCadastro.addActionListener {

            cardLayout.show(cardsPanel, "CADASTRO")
        }

        // COMPONENTES
        panel.add(titulo)

        panel.add(loginLabel)
        panel.add(loginField)

        panel.add(emailLabel)
        panel.add(emailField)

        panel.add(senhaLabel)
        panel.add(senhaField)

        panel.add(btnLogin)
        panel.add(irCadastro)

        return panel
    }

    JPanel createCadastroPanel() {

        JPanel panel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g)

                if (backgroundImage != null) {

                    g.drawImage(
                            backgroundImage,
                            0,
                            0,
                            getWidth(),
                            getHeight(),
                            null
                    )
                }
            }
        }

        panel.setLayout(null)
        panel.setOpaque(false)

        // =========================
        // CENTRALIZAÇÃO
        // =========================

        int centerX = WIDTH / 2

        int fieldWidth = 320
        int fieldHeight = 45

        int labelX = centerX - 250
        int fieldX = centerX - 80

        // TITULO
        JLabel titulo = new JLabel("CADASTRO")

        titulo.setBounds(centerX - 150, 70, 400, 50)
        titulo.setFont(new Font("Arial", Font.BOLD, 46))
        titulo.setForeground(Color.WHITE)

        // NOME
        JLabel nomeLabel = createLabel("Nome", 220, labelX)

        nomeCadastroField = new JTextField()
        nomeCadastroField.setBounds(fieldX, 220, fieldWidth, fieldHeight)

        styleField(nomeCadastroField)

        // EMAIL
        JLabel emailLabel = createLabel("Email", 300, labelX)

        emailCadastroField = new JTextField()
        emailCadastroField.setBounds(fieldX, 300, fieldWidth, fieldHeight)

        styleField(emailCadastroField)

        // SENHA
        JLabel senhaLabel = createLabel("Senha", 380, labelX)

        senhaCadastroField = new JPasswordField()
        senhaCadastroField.setBounds(fieldX, 380, fieldWidth, fieldHeight)

        styleField(senhaCadastroField)

        // BOTAO CADASTRAR
        btnCadastro = new JButton("CADASTRAR")

        btnCadastro.setBounds(centerX - 110, 480, 220, 50)

        styleButton(btnCadastro)

        btnCadastro.addActionListener {

            String nome = nomeCadastroField.getText()
            String email = emailCadastroField.getText()
            String senha = new String(senhaCadastroField.getPassword())

            if (!nome.isEmpty()
                    && !email.isEmpty()
                    && !senha.isEmpty()) {

                println("[CADASTRO] Sucesso")
                println(nome)
                println(email)

                JOptionPane.showMessageDialog(
                        null,
                        "Cadastro realizado com sucesso!"
                )

                cardLayout.show(cardsPanel, "LOGIN")

            } else {

                JOptionPane.showMessageDialog(
                        null,
                        "Preencha todos os campos!"
                )
            }
        }

        // VOLTAR
        JButton voltarLogin = new JButton("VOLTAR")

        voltarLogin.setBounds(centerX - 110, 550, 220, 45)

        styleButton(voltarLogin)

        voltarLogin.addActionListener {

            cardLayout.show(cardsPanel, "LOGIN")
        }

        // COMPONENTES
        panel.add(titulo)

        panel.add(nomeLabel)
        panel.add(nomeCadastroField)

        panel.add(emailLabel)
        panel.add(emailCadastroField)

        panel.add(senhaLabel)
        panel.add(senhaCadastroField)

        panel.add(btnCadastro)
        panel.add(voltarLogin)

        return panel
    }

    JLabel createLabel(String text, int y, int x) {

        JLabel label = new JLabel(text)

        label.setBounds(x, y, 120, 40)

        label.setForeground(Color.WHITE)

        label.setFont(
                new Font("Arial", Font.BOLD, 22)
        )

        return label
    }

    void styleField(JTextField field) {

        field.setFont(
                new Font("Arial", Font.PLAIN, 20)
        )

        field.setBackground(
                new Color(30, 20, 10, 220)
        )

        field.setForeground(Color.WHITE)

        field.setCaretColor(Color.WHITE)

        field.setBorder(
                BorderFactory.createLineBorder(
                        new Color(180, 120, 60),
                        2
                )
        )
    }

    void styleButton(JButton button) {

        button.setFocusPainted(false)

        button.setFont(
                new Font("Arial", Font.BOLD, 20)
        )

        button.setBackground(
                new Color(120, 70, 20)
        )

        button.setForeground(Color.WHITE)

        button.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        )

        button.addMouseListener(new MouseAdapter() {

            @Override
            void mouseEntered(MouseEvent e) {

                button.setBackground(
                        new Color(160, 100, 40)
                )
            }

            @Override
            void mouseExited(MouseEvent e) {

                button.setBackground(
                        new Color(120, 70, 20)
                )
            }
        })
    }
}
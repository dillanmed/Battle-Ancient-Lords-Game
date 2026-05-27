// =========================================
// IMPORTS
// =========================================

import de.gurkenlabs.litiengine.Game
import de.gurkenlabs.litiengine.gui.screens.Screen

import javax.imageio.ImageIO
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

import javazoom.jl.player.Player

import java.awt.AlphaComposite
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Composite
import java.awt.Font
import java.awt.GraphicsEnvironment
import java.awt.GradientPaint
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.Toolkit

import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

import java.awt.image.BufferedImage

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

import java.util.Random
import java.util.List
import java.util.ArrayList

class Main {

    // =========================================
// ADICIONE ISSO JUNTO DAS VARIÁVEIS GLOBAIS
// =========================================

// Boss final
    static boolean isBossFight = false
    static String bossName = ""
    static int bossHP = 0
    static int bossMaxHP = 0

// Floating Damage Text
    static class FloatingText {
        String text
        int x
        int y
        Color color
        long startTime

        FloatingText(String text, int x, int y, Color color) {
            this.text = text
            this.x = x
            this.y = y
            this.color = color
            this.startTime = System.currentTimeMillis()
        }
    }

    static List<FloatingText> floatingTexts = new ArrayList<>()

    // =========================================
    // SCREEN SIZE
    // =========================================
    static int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width
    static int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height

    // =========================================
    // GAME STATES
    // =========================================
    static final int MENU = 0
    static final int CHARACTER_SELECT = 1
    static final int BATTLE = 2
    static final int GAME_OVER = 3
    static final int WIN = 4
    static final int INVENTORY = 5

    static int gameState = MENU
    static int previousState = BATTLE
    static int currentPhase = 1

    // =========================================
    // MENU & UI OPTIONS
    // =========================================
    static int selectedMenu = 0
    static String[] menuOptions = ["START GAME", "QUIT GAME"]

    static int selectedGameOverOpt = 0
    static String[] gameOverOptions = ["RETRY (RESET)", "QUIT TO DESKTOP"]

    static int selectedWinOpt = 0
    static String[] winOptions = ["PLAY AGAIN", "QUIT TO DESKTOP"]

    // =========================================
    // INVENTORY SYSTEM
    // =========================================
    static List<String> inventoryItems = ["Espada de Mitril", "Pocao de Vida Extra", "Elixir de Mana", "Escudo Ancestral", "Anel do Dracao", "Capa da Invisibilidade"]
    static List<Integer> inventoryQuantities = [1, 5, 3, 1, 1, 2]

    // =========================================
    // CHARACTER SELECT
    // =========================================
    static int selectedCharacter = 0
    static String[] characters = ["WARRIOR", "ARCHER", "MAGE"]
    static String currentClass = "WARRIOR"

    // =========================================
    // PLAYER LIVES & RPG STATS
    // =========================================
    static int playerHP = 100
    static int maxHP = 100
    static int playerMana = 50
    static int maxMana = 50

    static int playerLevel = 1
    static int playerXP = 0
    static int maxXP = 100
    static int damageBonus = 0

    static int playerLives = 5
    static boolean isRespawning = false
    static long respawnStartTime = 0
    static final long RESPAWN_BLINK_DURATION = 2500

    // =========================================
    // ENEMIES
    // =========================================
    static int[] enemyHP = [40, 40, 40]
    static int maxEnemyHP = 40
    static String[] enemyNames = ["Inimigo 1", "Inimigo 2", "Inimigo 3"]

    // =========================================
    // TURN & INTRO SYSTEM
    // =========================================
    static boolean playerTurn = false
    static int selectedEnemy = 0
    static boolean introActive = false
    static boolean phaseTransitionActive = false

    static float currentPlayerX = -200f
    static float currentPlayerY = 0f
    static float[][] currentEnemyPos = [ [0f, 0f], [0f, 0f], [0f, 0f] ]

    static final int TARGET_PLAYER_X = 160
    static int[][] TARGET_ENEMY_POS = [ [0, 0], [0, 0], [0, 0] ]

    // =========================================
    // MESSAGE
    // =========================================
    static String battleMessage = "LEFT / RIGHT = SELECT | ENTER = ATTACK"

    static Random random = new Random()

    // Controladores de debug para evitar spam no console
    static int lastPlayerFrameIdx = -1
    static String lastPlayerStateStr = ""
    static int[] lastEnemyFrameIdx = [-1, -1, -1]
    static String[] lastEnemyStateStr = ["", "", ""]

    // =========================================
    // FONTE CUSTOMIZADA
    // =========================================
    static Font gameFontBase = null

    static void loadGameFont() {
        try {
            InputStream is = Main.class.getResourceAsStream("/fonte/Pixel Letras.ttf")
            if (is == null) {
                is = Main.class.getResourceAsStream("/resources/fonte/Pixel Letras.ttf")
            }
            File fontFile = new File("src/main/resources/fonte/Pixel Letras.ttf")
            if (is != null) {
                gameFontBase = Font.createFont(Font.TRUETYPE_FONT, is)
            } else if (fontFile.exists()) {
                gameFontBase = Font.createFont(Font.TRUETYPE_FONT, fontFile)
            } else {
                gameFontBase = new Font("SansSerif", Font.BOLD, 12)
                return
            }
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
            ge.registerFont(gameFontBase)
        } catch (Exception e) {
            gameFontBase = new Font("SansSerif", Font.BOLD, 12)
        }
    }

    static Font getGameFont(int style, float size) {
        if (gameFontBase != null) return gameFontBase.deriveFont(style, size)
        return new Font("SansSerif", style, (int)size)
    }

    // =========================================
    // AUDIO ENGINE
    // =========================================
    static Clip menuWavClip = null
    static Player battleMp3Player = null
    static volatile boolean loopBattleMusic = false
    static Thread battleMusicThread = null

    static void playMenuMusic(String path) {
        stopAllMusic()
        Thread.start {
            try {
                InputStream is = Main.class.getResourceAsStream("/" + path.replace("src/main/resources/", ""))
                if (is != null) {
                    BufferedInputStream bis = new BufferedInputStream(is)
                    AudioInputStream audio = AudioSystem.getAudioInputStream(bis)
                    menuWavClip = AudioSystem.getClip()
                    menuWavClip.open(audio)
                } else {
                    File file = new File(path)
                    if (file.exists()) {
                        AudioInputStream audio = AudioSystem.getAudioInputStream(file)
                        menuWavClip = AudioSystem.getClip()
                        menuWavClip.open(audio)
                    }
                }
                if (menuWavClip != null) {
                    menuWavClip.loop(Clip.LOOP_CONTINUOUSLY)
                    menuWavClip.start()
                }
            } catch (Exception e) {
                println "Erro ao tocar música do menu: " + e.message
            }
        }
    }

    static void playBattleMusic(String path) {
        stopAllMusic()
        loopBattleMusic = true

        battleMusicThread = Thread.start {
            while (loopBattleMusic) {
                try {
                    InputStream is = Main.class.getResourceAsStream("/" + path.replace("src/main/resources/", ""))
                    BufferedInputStream bis = null

                    if (is != null) {
                        bis = new BufferedInputStream(is)
                    } else {
                        File file = new File(path)
                        if (file.exists()) {
                            bis = new BufferedInputStream(new FileInputStream(file))
                        }
                    }

                    if (bis != null) {
                        battleMp3Player = new Player(bis)
                        battleMp3Player.play()
                        bis.close()
                    } else {
                        Thread.sleep(500)
                    }
                } catch (Exception e) {
                    try { Thread.sleep(500) } catch(Exception ex){}
                }
            }
        }
    }

    static void stopAllMusic() {
        loopBattleMusic = false
        if (menuWavClip != null) {
            try {
                if (menuWavClip.isRunning()) menuWavClip.stop()
                menuWavClip.close()
            } catch (Exception e) {}
            menuWavClip = null
        }
        if (battleMp3Player != null) {
            try {
                battleMp3Player.close()
            } catch (Exception e) {}
            battleMp3Player = null
        }
        if (battleMusicThread != null) {
            try { battleMusicThread.interrupt() } catch (Exception e) {}
            battleMusicThread = null
        }
    }

    static void playSFX(String path) {
        Thread.start {
            try {
                InputStream is = Main.class.getResourceAsStream("/" + path.replace("src/main/resources/", ""))
                if (is != null) {
                    BufferedInputStream bis = new BufferedInputStream(is)
                    if (path.toLowerCase().endsWith(".wav")) {
                        AudioInputStream audioStream = AudioSystem.getAudioInputStream(bis)
                        Clip sfxClip = AudioSystem.getClip()
                        sfxClip.open(audioStream)
                        sfxClip.start()
                    } else {
                        Player sfxPlayer = new Player(bis)
                        sfxPlayer.play()
                    }
                    return
                }
                File file = new File(path)
                if (!file.exists()) return
                if (path.toLowerCase().endsWith(".wav")) {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(file)
                    Clip sfxClip = AudioSystem.getClip()
                    sfxClip.open(audioStream)
                    sfxClip.start()
                } else if (path.toLowerCase().endsWith(".mp3")) {
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))
                    Player sfxPlayer = new Player(bis)
                    sfxPlayer.play()
                    bis.close()
                }
            } catch (Exception e) {
                println "Erro SFX: " + e.getMessage()
            }
        }
    }

    // =========================================
    // ANIMATION & CONSTANTS
    // =========================================
    static int animationTick = 0

    static final int STATE_IDLE = 0
    static final int STATE_WALK = 1
    static final int STATE_ATTACK = 2
    static final int STATE_HURT = 3
    static final int STATE_DEAD = 4

    static int playerVisualState = STATE_IDLE
    static long playerStateTime = 0

    static int[] enemyVisualState = [STATE_IDLE, STATE_IDLE, STATE_IDLE]
    static long[] enemyStateTime = [0L, 0L, 0L]

    static final long HIT_FLASH_DURATION = 400
    static final long DEATH_FADE_DURATION = 1500

    // =========================================
    // CACHING
    // =========================================
    static BufferedImage playerCachedHurtFrame = null
    static BufferedImage[] enemyCachedHurtFrames = [null, null, null]

    // =========================================
    // SPRITES & BACKGROUNDS ARRAYS
    // =========================================
    static List<BufferedImage> playerIdle = new ArrayList<>(), playerWalk = new ArrayList<>(), playerAttack = new ArrayList<>(), playerHurt = new ArrayList<>(), playerDead = new ArrayList<>()

    static List<BufferedImage> menuPreviewWarrior = new ArrayList<> ()
    static List<BufferedImage> menuPreviewArcher = new ArrayList<> ()
    static List<BufferedImage> menuPreviewMage = new ArrayList<> ()

    static List<List<BufferedImage>> enemyIdles = [new ArrayList<>(), new ArrayList<>(), new ArrayList<>()]
    static List<List<BufferedImage>> enemyWalks = [new ArrayList<>(), new ArrayList<>(), new ArrayList<>()]
    static List<List<BufferedImage>> enemyAttacks = [new ArrayList<>(), new ArrayList<>(), new ArrayList<>()]
    static List<List<BufferedImage>> enemyHurts = [new ArrayList<>(), new ArrayList<>(), new ArrayList<>()]
    static List<List<BufferedImage>> enemyDeads = [new ArrayList<>(), new ArrayList<>(), new ArrayList<>()]

    static BufferedImage[] phaseBackgrounds = new BufferedImage[6]
    static BufferedImage menuBackground

    // =========================================
    // CUSTOM SCREEN
    // =========================================
    static class GameScreen extends Screen {
        protected GameScreen() {
            super("GAME_SCREEN")
        }

        @Override
        public void render(final Graphics2D g) {
            super.render(g)
            Main.animationTick++
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

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
            } else {
                Main.updatePositions()
                Main.renderBattle(g)
            }
        }
    }

    // =========================================
    // MAIN
    // =========================================
    static void main(String[] args) {
        String[] emptyArgs = new String[0]
        Game.init(emptyArgs)

        Game.window().setTitle("Battle of the Ancient Lords")
        Game.window().getRenderComponent().setSize(WIDTH, HEIGHT)

        loadGameFont()
        loadSprites()

        playMenuMusic("src/main/resources/sons/musica da tela inicial.wav")

        int hudY = HEIGHT - 240
        int enemyFloorY = hudY - 240
        TARGET_ENEMY_POS = [
                [WIDTH - 550, enemyFloorY - 45],
                [WIDTH - 350, enemyFloorY - 5],
                [WIDTH - 480, enemyFloorY + 35]
        ]
        currentPlayerY = hudY - 240

        GameScreen customScreen = new GameScreen()
        Game.screens().add(customScreen)
        Game.screens().display(customScreen)

        Game.start()

        def canvas = Game.window().getRenderComponent()
        canvas.setFocusable(true)
        canvas.requestFocus()

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            void keyPressed(KeyEvent e) {
                // Teclas Globais de Inventário (Apenas acessível se estiver jogando ou já no inventário)
                if (e.getKeyCode() == KeyEvent.VK_M) {
                    if (gameState == BATTLE) {
                        playSFX("src/main/resources/sons/Abrindo inventario.wav")
                        previousState = gameState
                        gameState = INVENTORY
                        println "[INVENTARIO] Painel aberto. Itens carregados no Console."
                    }
                    return
                }

                if (e.getKeyCode() == KeyEvent.VK_N) {
                    if (gameState == INVENTORY) {
                        gameState = previousState
                        println "[INVENTARIO] Retornando ao combate."
                    }
                    return
                }

                if (gameState == MENU) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            selectedMenu = (selectedMenu - 1 + menuOptions.length) % menuOptions.length
                            break
                        case KeyEvent.VK_DOWN:
                            selectedMenu = (selectedMenu + 1) % menuOptions.length
                            break
                        case KeyEvent.VK_ENTER:
                            if (selectedMenu == 0) {
                                gameState = CHARACTER_SELECT
                            } else {
                                System.exit(0)
                            }
                            break
                    }
                    return
                }

                if (gameState == CHARACTER_SELECT) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_LEFT:
                            selectedCharacter = (selectedCharacter - 1 + 3) % 3
                            break
                        case KeyEvent.VK_RIGHT:
                            selectedCharacter = (selectedCharacter + 1) % 3
                            break
                        case KeyEvent.VK_ENTER:
                            currentClass = characters[selectedCharacter]
                            println "[DIAGNOSTICO CLASSE] Selecionou a classe: " + currentClass
                            resetPlayerStatsCompletely()
                            loadActivePlayerSprites()

                            stopAllMusic()
                            currentPhase = 1
                            generateNewEnemiesForNextPhase()
                            startBattleIntro()

                            gameState = BATTLE
                            playBattleMusic("src/main/resources/sons/Musica de batalha.mp3")
                            break
                    }
                    return
                }

                if (gameState == GAME_OVER) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            selectedGameOverOpt = (selectedGameOverOpt - 1 + gameOverOptions.length) % gameOverOptions.length
                            break
                        case KeyEvent.VK_DOWN:
                            selectedGameOverOpt = (selectedGameOverOpt + 1) % gameOverOptions.length
                            break
                        case KeyEvent.VK_ENTER:
                            if (selectedGameOverOpt == 0) {
                                resetPlayerStatsCompletely()
                                loadActivePlayerSprites()
                                currentPhase = 1
                                generateNewEnemiesForNextPhase()
                                startBattleIntro()
                                gameState = BATTLE
                                playBattleMusic("src/main/resources/sons/Musica de batalha.mp3")
                            } else {
                                System.exit(0)
                            }
                            break
                    }
                    return
                }

                if (gameState == WIN) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            selectedWinOpt = (selectedWinOpt - 1 + winOptions.length) % winOptions.length
                            break
                        case KeyEvent.VK_DOWN:
                            selectedWinOpt = (selectedWinOpt + 1) % winOptions.length
                            break
                        case KeyEvent.VK_ENTER:
                            if (selectedWinOpt == 0) {
                                stopAllMusic()
                                gameState = MENU
                                playMenuMusic("src/main/resources/sons/musica da tela inicial.wav")
                            } else {
                                System.exit(0)
                            }
                            break
                    }
                    return
                }

                if (gameState == INVENTORY) {
                    return // Trava comandos padrões de luta se estiver no inventário
                }

                if (!playerTurn || introActive || phaseTransitionActive || isRespawning) return

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        do {
                            selectedEnemy = (selectedEnemy - 1 + 3) % 3
                        } while (enemyHP[selectedEnemy] <= 0 && !allEnemiesDead())
                        break
                    case KeyEvent.VK_RIGHT:
                        do {
                            selectedEnemy = (selectedEnemy + 1) % 3
                        } while (enemyHP[selectedEnemy] <= 0 && !allEnemiesDead())
                        break
                    case KeyEvent.VK_ENTER:
                        attackEnemy(selectedEnemy)
                        break
                    case KeyEvent.VK_SPACE:
                        executeClassSpecialSkill()
                        break
                }
            }
        })
    }

    // =========================================
    // HABILIDADES EXCLUSIVAS DE CLASSE
    // =========================================
    static void executeClassSpecialSkill() {
        if (currentClass == "WARRIOR") {
            if (playerMana < 15) {
                battleMessage = "Mana insuficiente para Grito de Guerra! (Precisa de 15 MP)"
                return
            }
            playerMana -= 15
            playerVisualState = STATE_ATTACK
            playerStateTime = System.currentTimeMillis()

            int curaFortalecida = 25 + (playerLevel * 5)
            playerHP = Math.min(maxHP, playerHP + curaFortalecida)

            playSFX("src/main/resources/sons/grito-de-guerra.mp3")
            battleMessage = "Guerreiro ativou [GRITO DE GUERRA] Lv.${playerLevel}! Recuperou ${curaFortalecida} de HP!"

        } else if (currentClass == "ARCHER") {
            if (playerMana < 20) {
                battleMessage = "Mana insuficiente para Chuva de Flechas! (Precisa de 20 MP)"
                return
            }
            playerMana -= 20
            playerVisualState = STATE_ATTACK
            playerStateTime = System.currentTimeMillis()

            playSFX("src/main/resources/sons/Dano no inimigo.mp3")
            battleMessage = "Arqueiro conjurou [CHUVA DE FLECHAS]! Dano aplicado em TODOS os alvos!"

            for (int i = 0; i < 3; i++) {
                if (enemyHP[i] > 0) {
                    int damage = 8 + damageBonus + random.nextInt(10)
                    enemyHP[i] -= damage
                    if (enemyHP[i] <= 0) {
                        enemyHP[i] = 0
                        enemyVisualState[i] = STATE_DEAD
                        enemyStateTime[i] = System.currentTimeMillis()
                        grantKillRewards(i)
                    } else {
                        enemyVisualState[i] = STATE_HURT
                        enemyStateTime[i] = System.currentTimeMillis()
                    }
                }
            }

        } else if (currentClass == "MAGE") {
            if (playerMana < 35) {
                battleMessage = "Mana insuficiente para Meteoro Arcano! (Precisa de 35 MP)"
                return
            }
            if (enemyHP[selectedEnemy] <= 0) {
                battleMessage = "Selecione um alvo vivo para o Meteoro!"
                return
            }
            playerMana -= 35
            playerVisualState = STATE_ATTACK
            playerStateTime = System.currentTimeMillis()

            int massiveDamage = (10 + damageBonus + random.nextInt(15)) * 2
            enemyHP[selectedEnemy] -= massiveDamage
            playSFX("src/main/resources/sons/Dano no inimigo.mp3")

            battleMessage = "Mago invocou [METEORO ARCANO] causando devastadores ${massiveDamage} no alvo!"

            if (enemyHP[selectedEnemy] <= 0) {
                enemyHP[selectedEnemy] = 0
                enemyVisualState[selectedEnemy] = STATE_DEAD
                enemyStateTime[selectedEnemy] = System.currentTimeMillis()
                grantKillRewards(selectedEnemy)
            } else {
                enemyVisualState[selectedEnemy] = STATE_HURT
                enemyStateTime[selectedEnemy] = System.currentTimeMillis()
            }
        }

        playerTurn = false
        Thread.start {
            Thread.sleep(1500)
            if (allEnemiesDead()) {
                if (currentPhase < 6) {
                    startPhaseTransition()
                } else {
                    stopAllMusic()
                    selectedWinOpt = 0
                    gameState = WIN
                }
            } else {
                enemyTurn()
            }
        }
    }

    static void resetPlayerStatsCompletely() {
        playerLives = 5
        isRespawning = false
        playerLevel = 1
        playerXP = 0
        maxXP = 100
        damageBonus = 0

        if (currentClass == "WARRIOR") {
            maxHP = 120; playerHP = 120
            maxMana = 30; playerMana = 30
        } else if (currentClass == "ARCHER") {
            maxHP = 90; playerHP = 90
            maxMana = 60; playerMana = 60
        } else {
            maxHP = 70; playerHP = 70
            maxMana = 120; playerMana = 120
        }
    }

    static void loadActivePlayerSprites() {
        println "\n--- CARREGANDO SPRITES DO JOGADOR ATIVO (${currentClass}) ---"
        String prefix = (currentClass == "ARCHER" ? "Arqueiro" : currentClass == "MAGE" ? "Mago" : "Guerreiro")

        playerIdle   = loadFramesFromFolder("src/main/resources/sprites/sprite guerreiros/Guerreiros/" + prefix + "/idle")
        playerWalk   = loadFramesFromFolder("src/main/resources/sprites/sprite guerreiros/Guerreiros/" + prefix + "/walk")
        playerAttack = loadFramesFromFolder("src/main/resources/sprites/sprite guerreiros/Guerreiros/" + prefix + "/attack")
        playerHurt   = loadFramesFromFolder("src/main/resources/sprites/sprite guerreiros/Guerreiros/" + prefix + "/hurt")
        playerDead   = loadFramesFromFolder("src/main/resources/sprites/sprite guerreiros/Guerreiros/" + prefix + "/dead")
        playerCachedHurtFrame = null
        println "---------------------------------------------------------\n"
    }

    static void startBattleIntro() {
        introActive = true
        playerTurn = false
        battleMessage = "Entrando na Arena da Fase ${currentPhase}..."

        currentPlayerX = -250f
        playerVisualState = STATE_WALK
        playerStateTime = System.currentTimeMillis()

        for (int i = 0; i < 3; i++) {
            currentEnemyPos[i][0] = (float) (WIDTH + 100 + (i * 120))
            currentEnemyPos[i][1] = (float) TARGET_ENEMY_POS[i][1]
            enemyVisualState[i] = STATE_WALK
            enemyStateTime[i] = System.currentTimeMillis()
        }
    }

    static void startPhaseTransition() {
        phaseTransitionActive = true
        playerTurn = false
        battleMessage = "Area limpa! Avancando para a proxima fase..."

        playerVisualState = STATE_WALK
        playerStateTime = System.currentTimeMillis()
    }

    static void updatePositions() {
        float speed = 5.0f

        if (isRespawning) {
            long elapsed = System.currentTimeMillis() - respawnStartTime
            if (elapsed >= RESPAWN_BLINK_DURATION) {
                isRespawning = false
            }
        }

        if (phaseTransitionActive) {
            if (currentPlayerX < WIDTH + 200) {
                currentPlayerX += speed
            } else {
                phaseTransitionActive = false
                currentPhase++
                generateNewEnemiesForNextPhase()
                startBattleIntro()
            }
            return
        }

        if (!introActive) return

        boolean allInPosition = true

        if (currentPlayerX < TARGET_PLAYER_X) {
            currentPlayerX += speed
            if (currentPlayerX > TARGET_PLAYER_X) currentPlayerX = TARGET_PLAYER_X
            allInPosition = false
        } else if (playerVisualState == STATE_WALK) {
            playerVisualState = STATE_IDLE
            playerStateTime = System.currentTimeMillis()
        }

        for (int i = 0; i < 3; i++) {
            float targetX = TARGET_ENEMY_POS[i][0]
            if (currentEnemyPos[i][0] > targetX) {
                currentEnemyPos[i][0] -= speed
                if (currentEnemyPos[i][0] < targetX) currentEnemyPos[i][0] = targetX
                allInPosition = false
            } else if (enemyVisualState[i] == STATE_WALK) {
                enemyVisualState[i] = STATE_IDLE
                enemyStateTime[i] = System.currentTimeMillis()
            }
        }

        if (allInPosition) {
            introActive = false
            playerTurn = true
            battleMessage = "LEFT / RIGHT = SELECIONAR ALVO | ENTER = ATACAR | SPACE = ESPECIAL"
        }
    }

    static void generateNewEnemiesForNextPhase() {

        println "\n--- GERANDO INIMIGOS PARA A FASE ${currentPhase} ---"

        String baseEnemies = "src/main/resources/sprites/Sprite inimigos/"

        // =========================================
        // FASE 6 = BOSS FINAL
        // =========================================
        if (currentPhase == 6) {

            isBossFight = true

            bossName = "MINOTAURO ANCESTRAL"

            bossMaxHP = 850
            bossHP = bossMaxHP

            maxEnemyHP = bossMaxHP

            enemyNames[0] = bossName
            enemyHP[0] = bossHP

            enemyHP[1] = 0
            enemyHP[2] = 0

            String path = baseEnemies + "Minotauro/"

            enemyIdles[0]   = loadFramesFromFolder(path + "idle")
            enemyWalks[0]   = loadFramesFromFolder(path + "walk")
            enemyAttacks[0] = loadFramesFromFolder(path + "attack")
            enemyHurts[0]   = loadFramesFromFolder(path + "hurt")
            enemyDeads[0]   = loadFramesFromFolder(path + "dead")

            enemyVisualState[0] = STATE_IDLE

            println "[BOSS FINAL] Minotauro carregado."
            return
        }

        // =========================================
        // FASES NORMAIS
        // =========================================

        isBossFight = false

        String[] disponiveis = [
                "esqueleto",
                "esqueleto warrior",
                "Gorgon Dark",
                "Medusa",
                "Wolfman"
        ]

        String[] nomesExibicao = [
                "Esqueleto",
                "Esqueleto Warrior",
                "Gorgon Dark",
                "Medusa",
                "Wolfman"
        ]

        Random rand = new Random()

        maxEnemyHP = 35 + (currentPhase * 15)

        for (int i = 0; i < 3; i++) {

            int indiceSorteado = rand.nextInt(disponiveis.length)

            String pastaSorteada = disponiveis[indiceSorteado]

            enemyNames[i] = nomesExibicao[indiceSorteado] + " Nivel " + currentPhase

            enemyHP[i] = maxEnemyHP

            enemyVisualState[i] = STATE_IDLE

            String path = baseEnemies + pastaSorteada + "/"

            enemyIdles[i]   = loadFramesFromFolder(path + "idle")
            enemyWalks[i]   = loadFramesFromFolder(path + "walk")
            enemyAttacks[i] = loadFramesFromFolder(path + "attack")
            enemyHurts[i]   = loadFramesFromFolder(path + "hurt")
            enemyDeads[i]   = loadFramesFromFolder(path + "dead")
        }

        selectedEnemy = 0
    }

    static boolean allEnemiesDead() {

        for (int i = 0; i < enemyHP.length; i++) {
            if (enemyHP[i] > 0) {
                return false
            }
        }

        return true
    }

    static BufferedImage loadImage(String path) {
        try {
            String resourcePath = path.replace("src/main/resources/", "")
            if (!resourcePath.startsWith("/")) resourcePath = "/" + resourcePath

            InputStream is = Main.class.getResourceAsStream(resourcePath)
            if (is != null) {
                return ImageIO.read(is)
            }

            File file = new File(path)
            if (file.exists()) {
                return ImageIO.read(file)
            }
        } catch (Exception e) {
            println "[ERRO CRITICO] Falha ao ler imagem estatica: " + path + " -> " + e.message
        }
        return null
    }

    static List<BufferedImage> loadFramesFromFolder(String rawPath) {
        List<BufferedImage> list = new ArrayList<> ()
        String path = rawPath.replace("sprite guerreiros.Guerreiros", "sprite guerreiros/Guerreiros").replace("\\", "/")
        File dir = new File(path)

        if (!dir.exists()) {
            if (path.contains("/attack")) dir = new File(path.replace("/attack", "/atack"))
            else if (path.contains("/atack")) dir = new File(path.replace("/atack", "/attack"))
        }

        if (!dir.exists() && (path.contains("/walk") || path.contains("/hurt") || path.contains("/dead") || path.contains("/attack") || path.contains("/atack"))) {
            dir = new File(path.replaceAll("/(walk|hurt|dead|attack|atack)", "/idle"))
        }

        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles()
            if (files != null) {
                Arrays.sort(files, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()))
                for (File file : files) {
                    String name = file.getName().toLowerCase()
                    if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                        try {
                            BufferedImage img = ImageIO.read(file)
                            if (img != null) list.add(img)
                        } catch (Exception e) {}
                    }
                }
            }
        }
        if (list.isEmpty()) {
            list.add(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB))
        }
        return list
    }

    static void loadSprites() {
        println "\n--- INICIANDO CARREGAMENTO DO BANCO DE SPRITES ---"
        menuPreviewWarrior = loadFramesFromFolder("src/main/resources/sprites/sprite guerreiros/Guerreiros/Guerreiro/idle")
        menuPreviewArcher  = loadFramesFromFolder("src/main/resources/sprites/sprite guerreiros/Guerreiros/Arqueiro/idle")
        menuPreviewMage    = loadFramesFromFolder("src/main/resources/sprites/sprite guerreiros/Guerreiros/Mago/idle")

        phaseBackgrounds[0] = loadImage("src/main/resources/Background/Background do jogo.png")
        phaseBackgrounds[1] = loadImage("src/main/resources/Background/fundo1.png")
        phaseBackgrounds[2] = loadImage("src/main/resources/Background/fundo2.png")
        phaseBackgrounds[3] = loadImage("src/main/resources/Background/fundo3.png")
        phaseBackgrounds[4] = loadImage("src/main/resources/Background/fundo4.png")
        phaseBackgrounds[5] = loadImage("src/main/resources/Background/fundo5.png")

        menuBackground = loadImage("src/main/resources/Background/menu.jpg")
        println "--- FIM DO CARREGAMENTO DO BANCO DE SPRITES ---\n"
    }

    static BufferedImage getAnimationFrame(List<BufferedImage> frames, int tickSpeed, boolean loop, long startTime, int fallbackState) {
        if (frames == null || frames.isEmpty()) return null
        long elapsedTicks = (System.currentTimeMillis() - startTime) / tickSpeed
        int index = (int) elapsedTicks
        int frameIndex = loop ? (index % frames.size()) : Math.min(index, frames.size() - 1)

        if (!loop && index >= frames.size()) {
            if (fallbackState == STATE_IDLE && playerHP > 0) playerVisualState = STATE_IDLE
            return frames.get(frames.size() - 1)
        }
        return frames.get(frameIndex)
    }

    static BufferedImage getEnemyAnimationFrame(int enemyIdx, List<BufferedImage> frames, int tickSpeed, boolean loop, long startTime) {
        if (frames == null || frames.isEmpty()) return null
        long elapsedTicks = (System.currentTimeMillis() - startTime) / tickSpeed
        int index = (int) elapsedTicks
        int frameIndex = loop ? (index % frames.size()) : Math.min(index, frames.size() - 1)

        if (!loop && index >= frames.size()) {
            if (enemyHP[enemyIdx] <= 0) return frames.get(frames.size() - 1)
            if (enemyHP[enemyIdx] > 0) enemyVisualState[enemyIdx] = STATE_IDLE
            return frames.get(frames.size() - 1)
        }
        return frames.get(frameIndex)
    }

    static BufferedImage generateTintedSilhouette(BufferedImage original) {
        if (original == null || original.getWidth() <= 1 || original.getHeight() <= 1) return null
        int w = original.getWidth()
        int h = original.getHeight()
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int argb = original.getRGB(x, y)
                int a = (argb >> 24) & 0xFF
                if (a > 20) {
                    result.setRGB(x, y, (a << 24) | (255 << 16))
                }
            }
        }
        return result
    }

    static void drawSpriteWithEffects(Graphics2D mainG, BufferedImage originalFrame, int x, int y, int width, int height, long stateTime, int visualState, BufferedImage cachedHurt, boolean isPlayer) {
        long elapsed = System.currentTimeMillis() - stateTime
        if (isPlayer && isRespawning) {
            int blinkTick = (int) ((System.currentTimeMillis() - respawnStartTime) / 150)
            if (blinkTick % 2 == 0) return
        }
        if (visualState == STATE_DEAD) {
            if (elapsed >= DEATH_FADE_DURATION) return
            if (((int)(elapsed / 120)) % 2 == 0) return
            if (originalFrame != null && originalFrame.getWidth() > 1) mainG.drawImage(originalFrame, x, y, width, height, null)
            return
        }
        if (originalFrame == null || originalFrame.getWidth() <= 1) return
        if (visualState == STATE_HURT && elapsed < HIT_FLASH_DURATION) {
            if (cachedHurt != null) mainG.drawImage(cachedHurt, x, y, width, height, null)
            else mainG.drawImage(originalFrame, x, y, width, height, null)
        } else {
            mainG.drawImage(originalFrame, x, y, width, height, null)
        }
    }

    // =========================================
    // VISUAL RENDERS
    // =========================================
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
        // Renderiza o fundo de batalha levemente escurecido por trás do inventário
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

        // Título do Painel
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

            // Indicador Genérico de Sprite (Ícone falso)
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

        // Rodapé de instruções
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
                battleMessage + " | M = BAÚ DE ITENS",
                WIDTH / 2 - 240,
                hudY + 95
        )
    }

    // =========================================
    // COMBAT LOGIC
    // =========================================
    static void attackEnemy(int enemyIndex) {
        if (enemyHP[enemyIndex] <= 0) return

        playerVisualState = STATE_ATTACK
        playerStateTime = System.currentTimeMillis()

        int damage = 10 + damageBonus + random.nextInt(15)
        enemyHP[enemyIndex] -= damage
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
            enemyHP[enemyIndex] = 0
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

        playerTurn = false

        Thread.start {
            Thread.sleep(1500)
            if (allEnemiesDead()) {
                if (currentPhase < 6) {
                    startPhaseTransition()
                } else {
                    stopAllMusic()
                    selectedWinOpt = 0
                    gameState = WIN
                }
            } else {
                enemyTurn()
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
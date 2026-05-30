// =========================================
// IMPORTS
// =========================================
package game

import game.auth.EsqueceuSenha
import game.components.MainGameScreen
import game.effects.FloatingText
import game.services.AudioService
import game.services.FontService
import game.services.ServiceRegistry
import game.types.Personagem
import game.ui.LoginScreen
import game.ui.CadastroScreen

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
// ADICIONE ISSO JUNTO DAS VARIÃVEIS GLOBAIS
// =========================================

// Boss final
    static boolean isBossFight = false
    static String bossName = ""
    static int bossHP = 0
    static int bossMaxHP = 0

// Floating Damage Text
    static List<FloatingText> floatingTexts = new ArrayList<>()

    // =========================================
    // SCREEN SIZE
    // =========================================
    static int WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width
    static int HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height

    // =========================================
    // GAME STATES
    // =========================================
    static final int LOGIN = 0
    static final int MENU = 1
    static final int CHARACTER_SELECT = 2
    static final int BATTLE = 3
    static final int GAME_OVER = 4
    static final int WIN = 5
    static final int INVENTORY = 6

    static int gameState = LOGIN
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
    static FontService fontService = new FontService(Main)
    static AudioService audioService = new AudioService(Main)

    static void loadGameFont() {
        fontService.load()
    }

    static Font getGameFont(int style, float size) {
        fontService.get(style, size)
    }

    // =========================================
    // AUDIO ENGINE
    // =========================================
    static Clip menuWavClip = null
    static Player battleMp3Player = null
    static volatile boolean loopBattleMusic = false
    static Thread battleMusicThread = null

    static void playMenuMusic(String path) {
        audioService.playMenuMusic(path)
    }

    static void playBattleMusic(String path) {
        audioService.playBattleMusic(path)
    }

    static void stopAllMusic() {
        audioService.stopAllMusic()
    }

    static void playSFX(String path) {
        audioService.playSfx(path)
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

        LoginScreen loginScreen = new LoginScreen()
        CadastroScreen cadastroScreen = new CadastroScreen()
        EsqueceuSenha esqueceuSenhaScreen = new EsqueceuSenha()
        MainGameScreen customScreen = new MainGameScreen()

        Game.screens().add(loginScreen)
        Game.screens().add(cadastroScreen)
        Game.screens().add(esqueceuSenhaScreen)
        Game.screens().add(customScreen)

        Game.screens().display("login")

        // INICIA ENGINE PRIMEIRO
        Game.start()

        def canvas = Game.window().getRenderComponent()

        canvas.setFocusable(true)

        canvas.requestFocus()

        canvas.addKeyListener(new KeyAdapter() {
            @Override
            void keyPressed(KeyEvent e) {



                // Teclas Globais de InventÃ¡rio (Apenas acessÃ­vel se estiver jogando ou jÃ¡ no inventÃ¡rio)
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
                            if (!tryLoadBackendCharacter(currentClass)) {
                                resetPlayerStatsCompletely()
                            }
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
                    return // Trava comandos padrÃµes de luta se estiver no inventÃ¡rio
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
        game.combate.BattleController.executeClassSpecialSkill()
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

    static boolean tryLoadBackendCharacter(String selectedClass) {
        try {
            println "Tentando criar personagem no backend..."
            Personagem personagem = ServiceRegistry.personagemService.criarPersonagem(1L, "Heroi ${selectedClass}", selectedClass)
            if (personagem == null) {
                println "Falha ao criar personagem no backend. Usando fallback local."
                return false
            }

            applyFrontendCharacter(personagem)
            println "Personagem criado no backend com sucesso"
            return true
        } catch (Exception e) {
            println "Falha ao criar personagem no backend. Usando fallback local. Motivo: ${e.message}"
            return false
        }
    }

    static void applyFrontendCharacter(Personagem personagem) {
        currentClass = personagem.classe ?: currentClass
        playerLives = 5
        isRespawning = false
        playerLevel = personagem.nivel
        playerXP = personagem.xp
        maxXP = 100 * Math.max(playerLevel, 1)
        damageBonus = personagem.bonusDano
        maxHP = personagem.maxHp
        playerHP = personagem.hp
        maxMana = personagem.maxMana
        playerMana = personagem.mana
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
        return game.assets.GameAssets.loadImage(path)
    }
    static List<BufferedImage> loadFramesFromFolder(String rawPath) {
        return game.assets.GameAssets.loadFramesFromFolder(rawPath)
    }
    static void loadSprites() {
        game.assets.GameAssets.loadSprites()
    }
    static BufferedImage getAnimationFrame(List<BufferedImage> frames, int tickSpeed, boolean loop, long startTime, int fallbackState) {
        return game.assets.GameAssets.getAnimationFrame(frames, tickSpeed, loop, startTime, fallbackState)
    }
    static BufferedImage getEnemyAnimationFrame(int enemyIdx, List<BufferedImage> frames, int tickSpeed, boolean loop, long startTime) {
        return game.assets.GameAssets.getEnemyAnimationFrame(enemyIdx, frames, tickSpeed, loop, startTime)
    }
    static BufferedImage generateTintedSilhouette(BufferedImage original) {
        return game.assets.GameAssets.generateTintedSilhouette(original)
    }
    static void drawSpriteWithEffects(Graphics2D mainG, BufferedImage originalFrame, int x, int y, int width, int height, long stateTime, int visualState, BufferedImage cachedHurt, boolean isPlayer) {
        game.assets.GameAssets.drawSpriteWithEffects(mainG, originalFrame, x, y, width, height, stateTime, visualState, cachedHurt, isPlayer)
    }
    static void renderMenu(Graphics2D g) {
        game.render.GameRenderer.renderMenu(g)
    }
    static void renderCharacterSelect(Graphics2D g) {
        game.render.GameRenderer.renderCharacterSelect(g)
    }
    static void renderGameOver(Graphics2D g) {
        game.render.GameRenderer.renderGameOver(g)
    }
    static void renderWin(Graphics2D g) {
        game.render.GameRenderer.renderWin(g)
    }
    static void renderInventory(Graphics2D g) {
        game.render.GameRenderer.renderInventory(g)
    }
    static void renderBattle(Graphics2D g) {
        game.render.GameRenderer.renderBattle(g)
    }
    static void attackEnemy(int enemyIndex) {
        game.combate.BattleController.attackEnemy(enemyIndex)
    }
    static void grantKillRewards(int enemyIndex) {
        game.combate.BattleController.grantKillRewards(enemyIndex)
    }
    static void enemyTurn() {
        game.combate.BattleController.enemyTurn()
    }
}

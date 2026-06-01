// =========================================
// IMPORTS
// =========================================
package game

import game.ui.ConfiguracaoScreen
import game.inventory.Item
import game.inventory.inventario
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
    static List<String> inventoryItems = ["Pocao de Vida"]
    static List<Integer> inventoryQuantities = [5]
    static int selectedInventoryItem = 0
    static volatile boolean inventoryActionLoading = false
    static Set<Long> batalhasComPocaoUsada = [] as Set

    // =========================================
    // CHARACTER SELECT
    // =========================================
    static int selectedCharacter = 0
    static String[] characters = ["WARRIOR", "ARCHER", "MAGE"]
    static String currentClass = "WARRIOR"
    static Map<String, Map<String, Integer>> previewStatsPorClasse = [:]
    static boolean previewPersonagensCarregado = false
    static Long previewUsuarioIdCarregado = null
    static boolean previewPersonagensCarregando = false

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
    static Long currentPersonagemId = 1L
    static boolean personagemBackendCarregado = false

    static int playerLives = 5
    static boolean isRespawning = false
    static long respawnStartTime = 0
    static final long RESPAWN_BLINK_DURATION = 2500

    // =========================================
    // ENEMIES
    // =========================================
    static int[] enemyHP = [40, 40, 40]
    static int[] enemyMaxHP = [40, 40, 40]
    static Long[] enemyIds = [null, null, null]
    static int maxEnemyHP = 40
    static String[] enemyNames = ["Inimigo 1", "Inimigo 2", "Inimigo 3"]
    static Long currentBattleId = null

    // =========================================
    // TURN & INTRO SYSTEM
    // =========================================
    static boolean playerTurn = false
    static int selectedEnemy = 0
    static boolean introActive = false
    static boolean phaseTransitionActive = false
    static volatile boolean battleLoading = false
    static volatile boolean characterSelectionLoading = false
    static volatile boolean combatConnectionPaused = false
    static String battleLoadingTitle = "PREPARANDO ARENA"
    static String battleLoadingSubtitle = "AGUARDE..."
    static String phaseBannerTitle = ""
    static String phaseBannerSubtitle = ""
    static long phaseBannerUntil = 0L

    static float currentPlayerX = -200f
    static float currentPlayerY = 0f
    static float[][] currentEnemyPos = [ [0f, 0f], [0f, 0f], [0f, 0f] ]

    static final int TARGET_PLAYER_X = 160
    static int[][] TARGET_ENEMY_POS = [ [0, 0], [0, 0], [0, 0] ]

    // =========================================
    // MESSAGE
    // =========================================
    static String battleMessage = "LEFT / RIGHT = SELECT | ENTER = ATTACK"
    static List<String> combatLog = new ArrayList<>()

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
        inventario.loadStarterItems()

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
        Game.screens().add(new ConfiguracaoScreen())

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
                    if (gameState == BATTLE && podeAbrirInventario()) {
                        playSFX("src/main/resources/sons/Abrindo inventario.wav")
                        previousState = gameState
                        gameState = INVENTORY
                        println "[INVENTARIO] Painel aberto. Itens carregados no Console."
                    } else if (gameState == BATTLE) {
                        registrarMensagemCombate("Aguarde o turno ficar livre.")
                    }
                    return
                }

                if (e.getKeyCode() == KeyEvent.VK_N) {
                    if (gameState == INVENTORY && !inventoryActionLoading) {
                        gameState = previousState
                        println "[INVENTARIO] Retornando ao combate."
                    }
                    return
                }

                if (gameState == INVENTORY) {
                    switch (e.getKeyCode()) {
                        case KeyEvent.VK_UP:
                            if (!inventario.items.isEmpty()) {
                                selectedInventoryItem = (selectedInventoryItem - 1 + inventario.items.size()) % inventario.items.size()
                            }
                            break
                        case KeyEvent.VK_DOWN:
                            if (!inventario.items.isEmpty()) {
                                selectedInventoryItem = (selectedInventoryItem + 1) % inventario.items.size()
                            }
                            break
                        case KeyEvent.VK_ENTER:
                        case KeyEvent.VK_SPACE:
                            usarItemSelecionadoDoInventario()
                            break
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
                                carregarPreviewPersonagensUsuario()
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
                            if (characterSelectionLoading) break
                            characterSelectionLoading = true
                            currentClass = characters[selectedCharacter]
                            playerTurn = false
                            startBattleLoadingCutscene("PREPARANDO HEROI", "A ARENA ESTA SENDO CARREGADA")
                            gameState = BATTLE
                            println "[DIAGNOSTICO CLASSE] Selecionou a classe: " + currentClass
                            Thread.start {
                                try {
                                    if (!tryLoadBackendCharacter(currentClass)) {
                                        resetPlayerStatsCompletely()
                                    }
                                    loadActivePlayerSprites()

                                    stopAllMusic()
                                    currentPhase = 1
                                    generateNewEnemiesForNextPhase()
                                    startBattleIntro()

                                    playBattleMusic("src/main/resources/sons/Musica de batalha.mp3")
                                } finally {
                                    battleLoading = false
                                    characterSelectionLoading = false
                                }
                            }
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
                                if (battleLoading) break
                                resetPlayerStatsCompletely()
                                loadActivePlayerSprites()
                                currentPhase = 1
                                startBattleLoadingCutscene("PREPARANDO ARENA", "A FASE ${currentPhase} ESTA SENDO CARREGADA")
                                Thread.start {
                                    try {
                                        generateNewEnemiesForNextPhase()
                                        startBattleIntro()
                                        gameState = BATTLE
                                        playBattleMusic("src/main/resources/sons/Musica de batalha.mp3")
                                    } finally {
                                        battleLoading = false
                                    }
                                }
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

                if (bloqueiaInputCombate()) return

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
        personagemBackendCarregado = false

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
            Long usuarioId = obterUsuarioIdAtual()
            Personagem personagem = ServiceRegistry.personagemService.buscarPersonagemPorClasse(usuarioId, selectedClass)
            if (ServiceRegistry.personagemService.houveFalhaIntegracao()) {
                println "Falha ao integrar com servico-personagem. Usando fallback local."
                return false
            }

            if (personagem != null) {
                println "Personagem salvo encontrado para classe ${selectedClass}."
            } else {
                println "Nenhum personagem salvo encontrado. Criando novo..."
                personagem = ServiceRegistry.personagemService.criarPersonagemNoBackend(usuarioId, "Heroi ${selectedClass}", selectedClass)
                if (personagem == null) {
                    println "Falha ao integrar com servico-personagem. Usando fallback local."
                    return false
                }
            }

            Personagem dadosCombate = buscarDadosCombatePersonagem(personagem)
            if (dadosCombate != null) {
                personagem = dadosCombate
            }

            applyFrontendCharacter(personagem)
            return true
        } catch (Exception e) {
            println "Falha ao integrar com servico-personagem. Usando fallback local. Motivo: ${e.message}"
            return false
        }
    }

    static Long obterUsuarioIdAtual() {
        try {
            String id = ServiceRegistry.authContext?.usuarioAtual?.id
            if (id?.isLong()) {
                Long usuarioId = id as Long
                if (usuarioId > 0L) {
                    return usuarioId
                }
            }
        } catch (Exception ignored) {
        }

        return 1L
    }

    static void carregarPreviewPersonagensUsuario() {
        Long usuarioId = obterUsuarioIdAtual()

        if (previewPersonagensCarregado && previewUsuarioIdCarregado == usuarioId) {
            return
        }
        if (previewPersonagensCarregando && previewUsuarioIdCarregado == usuarioId) {
            return
        }

        previewPersonagensCarregando = true
        previewUsuarioIdCarregado = usuarioId
        previewPersonagensCarregado = false
        previewStatsPorClasse = [:]
        Long usuarioIdPreview = usuarioId

        Thread.start {
            try {
                println "Carregando preview de personagens do usuario..."
                List<Personagem> personagens = ServiceRegistry.personagemService.listarPersonagensPorUsuario(usuarioIdPreview)

                if (ServiceRegistry.personagemService.houveFalhaIntegracao()) {
                    println "Falha ao carregar preview do backend. Usando valores locais."
                    if (previewUsuarioIdCarregado == usuarioIdPreview) {
                        previewStatsPorClasse = [:]
                    }
                    return
                }

                Map<String, Map<String, Integer>> statsCarregados = [:]
                personagens.each { Personagem personagem ->
                    String classePreview = classeParaPreview(personagem?.classe)
                    if (classePreview) {
                        int hp = Math.max(personagem.maxHp, personagem.hp)
                        int mp = Math.max(personagem.maxMana, personagem.mana)
                        statsCarregados[classePreview] = [hp: hp, mp: mp]
                        println "Preview carregado para ${classePreview}: HP ${hp} / MP ${mp}"
                    }
                }

                if (previewUsuarioIdCarregado == usuarioIdPreview) {
                    previewStatsPorClasse = statsCarregados
                    characters.each { String classe ->
                        if (!previewStatsPorClasse.containsKey(classe)) {
                            println "Nenhum personagem salvo para ${classe}. Usando preview padrao."
                        }
                    }
                    previewPersonagensCarregado = true
                }
            } catch (Exception e) {
                if (previewUsuarioIdCarregado == usuarioIdPreview) {
                    previewStatsPorClasse = [:]
                }
                println "Falha ao carregar preview do backend. Usando valores locais."
                println "Erro ao carregar preview de personagens: ${e.message}"
            } finally {
                if (previewUsuarioIdCarregado == usuarioIdPreview) {
                    previewPersonagensCarregando = false
                }
            }
        }
    }

    static int obterHpPreviewClasse(String classe, int hpPadrao) {
        Map<String, Integer> stats = previewStatsPorClasse[classeParaPreview(classe)]
        stats?.hp ?: hpPadrao
    }

    static int obterMpPreviewClasse(String classe, int mpPadrao) {
        Map<String, Integer> stats = previewStatsPorClasse[classeParaPreview(classe)]
        stats?.mp ?: mpPadrao
    }

    static String textoHpPreviewClasse(String classe, int hpPadrao) {
        if (previewPersonagensCarregando) {
            return "..."
        }
        obterHpPreviewClasse(classe, hpPadrao).toString()
    }

    static String textoMpPreviewClasse(String classe, int mpPadrao) {
        if (previewPersonagensCarregando) {
            return "..."
        }
        obterMpPreviewClasse(classe, mpPadrao).toString()
    }

    static String classeParaPreview(String classe) {
        switch (classe?.trim()?.toUpperCase()) {
            case "GUERREIRO":
            case "WARRIOR":
                return "WARRIOR"
            case "ARQUEIRO":
            case "ARCHER":
                return "ARCHER"
            case "MAGO":
            case "MAGE":
                return "MAGE"
            default:
                return null
        }
    }

    static Personagem buscarDadosCombatePersonagem(Personagem personagem) {
        if (!personagem?.id) {
            println "Falha ao carregar dados de combate. Usando dados basicos do personagem."
            return null
        }

        println "Buscando dados de combate do personagem..."
        Personagem dadosCombate = ServiceRegistry.personagemService.buscarDadosCombate(personagem.id)
        if (dadosCombate == null) {
            println "Falha ao carregar dados de combate. Usando dados basicos do personagem."
            return null
        }

        println "Dados de combate carregados com sucesso."
        dadosCombate
    }

    static void applyFrontendCharacter(Personagem personagem) {
        currentClass = personagem.classe ?: currentClass
        if (personagem.id?.isLong()) {
            currentPersonagemId = personagem.id as Long
            personagemBackendCarregado = currentPersonagemId > 0L
        }
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
        showPhaseBanner(currentPhase)
        registrarMensagemCombate(currentPhase == 6 ? "BOSS FINAL - ${bossName}" : "FASE ${currentPhase} - NOVOS INIMIGOS APARECERAM")

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

    static void startBattleLoadingCutscene(String titulo, String subtitulo) {
        battleLoading = true
        playerTurn = false
        battleLoadingTitle = titulo
        battleLoadingSubtitle = subtitulo
        battleMessage = subtitulo

        currentPlayerX = -250f
        playerVisualState = STATE_IDLE
        playerStateTime = System.currentTimeMillis()

        for (int i = 0; i < 3; i++) {
            currentEnemyPos[i][0] = (float) (WIDTH + 180 + (i * 140))
            currentEnemyPos[i][1] = (float) TARGET_ENEMY_POS[i][1]
            enemyVisualState[i] = STATE_IDLE
            enemyStateTime[i] = System.currentTimeMillis()
        }
    }

    static void startPhaseTransition() {
        phaseTransitionActive = true
        playerTurn = false
        phaseBannerUntil = 0L
        registrarMensagemCombate("Area limpa! Avancando para a proxima fase...")

        playerVisualState = STATE_WALK
        playerStateTime = System.currentTimeMillis()
    }

    static void showPhaseBanner(int fase) {
        if (fase == 6) {
            phaseBannerTitle = "BOSS FINAL"
            phaseBannerSubtitle = bossName ?: "MINOTAURO ANCESTRAL"
            phaseBannerUntil = System.currentTimeMillis() + 1400L
            return
        }

        phaseBannerTitle = "FASE ${fase}"
        phaseBannerSubtitle = "NOVOS INIMIGOS APARECERAM"
        phaseBannerUntil = System.currentTimeMillis() + 1000L
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
            } else if (!battleLoading) {
                phaseTransitionActive = false
                currentPhase++
                startBattleLoadingCutscene("FASE ${currentPhase}", "NOVOS INIMIGOS ESTAO CHEGANDO")
                Thread.start {
                    try {
                        generateNewEnemiesForNextPhase()
                        startBattleIntro()
                    } finally {
                        battleLoading = false
                    }
                }
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
            if (System.currentTimeMillis() < phaseBannerUntil) {
                playerTurn = false
                return
            }

            phaseBannerUntil = 0L
            introActive = false
            playerTurn = true
            battleMessage = "LEFT / RIGHT = SELECIONAR ALVO | ENTER = ATACAR | SPACE = ESPECIAL"
        }
    }

    static void generateNewEnemiesForNextPhase() {

        println "\n--- GERANDO INIMIGOS PARA A FASE ${currentPhase} ---"

        String baseEnemies = "src/main/resources/sprites/Sprite inimigos/"
        Map batalhaBackend = carregarBatalhaDaFaseNoBackend()

        // =========================================
        // FASE 6 = BOSS FINAL
        // =========================================
        if (currentPhase == 6) {

            isBossFight = true

            bossName = nomeInimigoBackend(batalhaBackend, 0, "MINOTAURO ANCESTRAL")

            bossMaxHP = vidaMaximaInimigoBackend(batalhaBackend, 0, 850)
            bossHP = vidaAtualInimigoBackend(batalhaBackend, 0, bossMaxHP)

            maxEnemyHP = bossMaxHP
            enemyMaxHP[0] = bossMaxHP
            enemyIds[0] = idInimigoBackend(batalhaBackend, 0)

            enemyNames[0] = bossName
            enemyHP[0] = bossHP

            enemyHP[1] = 0
            enemyHP[2] = 0
            enemyMaxHP[1] = 0
            enemyMaxHP[2] = 0
            enemyIds[1] = null
            enemyIds[2] = null

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
        maxEnemyHP = 1

        for (int i = 0; i < 3; i++) {

            enemyNames[i] = nomeInimigoBackend(batalhaBackend, i, "Inimigo ${i + 1}")
            enemyHP[i] = vidaAtualInimigoBackend(batalhaBackend, i, 0)
            enemyMaxHP[i] = vidaMaximaInimigoBackend(batalhaBackend, i, enemyHP[i])
            enemyIds[i] = idInimigoBackend(batalhaBackend, i)
            maxEnemyHP = Math.max(maxEnemyHP, enemyMaxHP[i])

            enemyVisualState[i] = STATE_IDLE

            String path = baseEnemies + pastaSpriteInimigo(enemyNames[i]) + "/"

            enemyIdles[i]   = loadFramesFromFolder(path + "idle")
            enemyWalks[i]   = loadFramesFromFolder(path + "walk")
            enemyAttacks[i] = loadFramesFromFolder(path + "attack")
            enemyHurts[i]   = loadFramesFromFolder(path + "hurt")
            enemyDeads[i]   = loadFramesFromFolder(path + "dead")
        }

        selectedEnemy = 0
    }

    static Map carregarBatalhaDaFaseNoBackend() {
        try {
            Map batalha = ServiceRegistry.combateService.criarEIniciarBatalha(currentPersonagemId ?: 1L, currentPhase)
            currentBattleId = batalha.id as Long
            aplicarEstadoBatalhaBackend(batalha)
            return batalha
        } catch (Exception e) {
            currentBattleId = null
            registrarMensagemCombate("Falha ao iniciar batalha no servidor de combate.")
            println "Erro ao iniciar batalha no servico-combate: ${e.message}"
            return [inimigos: []]
        }
    }

    static boolean bloqueiaInputCombate() {
        !playerTurn ||
                introActive ||
                phaseTransitionActive ||
                battleLoading ||
                combatConnectionPaused ||
                game.combate.BattleController.battleActionInProgress ||
                isRespawning ||
                System.currentTimeMillis() < phaseBannerUntil
    }

    static boolean podeAbrirInventario() {
        gameState == BATTLE && !bloqueiaInputCombate()
    }

    static void registrarMensagemCombate(String mensagem) {
        if (mensagem == null || mensagem.trim().isEmpty()) {
            return
        }

        battleMessage = mensagem
        combatLog.add(0, mensagem)
        while (combatLog.size() > 3) {
            combatLog.remove(combatLog.size() - 1)
        }
    }

    static void feedbackSemMana(int custoMana) {
        registrarMensagemCombate("Mana insuficiente! Precisa de ${custoMana} MP.")
        floatingTexts.add(new FloatingText("SEM MANA", (int) currentPlayerX + 70, (int) currentPlayerY - 30, new Color(80, 170, 255)))
    }

    static void pausarCombatePorErro(String origem) {
        combatConnectionPaused = true
        playerTurn = false
        registrarMensagemCombate("Reconectando ao combate...")

        Thread.start("reconectar-combate") {
            boolean reconectado = false
            try {
                Thread.sleep(900)
                if (currentBattleId != null) {
                    Map batalha = ServiceRegistry.combateService.buscarBatalha(currentBattleId)
                    aplicarEstadoBatalhaBackend(batalha)
                    registrarMensagemCombate("Combate reconectado.")
                    playerTurn = true
                    reconectado = true
                }
            } catch (Exception e) {
                registrarMensagemCombate("Falha ao reconectar combate.")
                println "Falha ao reconectar combate (${origem}): ${e.message}"
            } finally {
                combatConnectionPaused = !reconectado
            }
        }
    }

    static void usarItemSelecionadoDoInventario() {
        if (inventoryActionLoading) {
            return
        }

        if (bloqueiaInputCombate()) {
            registrarMensagemCombate("Aguarde o turno ficar livre.")
            return
        }

        if (inventario.items.isEmpty()) {
            registrarMensagemCombate("Inventario vazio.")
            return
        }

        selectedInventoryItem = Math.min(selectedInventoryItem, inventario.items.size() - 1)
        Item item = inventario.items[selectedInventoryItem]

        if (item.nome != inventario.POCAO_VIDA) {
            registrarMensagemCombate("Item indisponivel.")
            return
        }

        if (playerHP >= maxHP) {
            registrarMensagemCombate("HP ja esta cheio.")
            return
        }

        if (currentBattleId != null && batalhasComPocaoUsada.contains(currentBattleId)) {
            registrarMensagemCombate("Voce ja usou uma pocao nesta batalha.")
            return
        }

        if (currentBattleId == null) {
            boolean usado = inventario.useItem(item.nome)
            if (usado) {
                registrarMensagemCombate("${item.nome} usada! +${inventario.CURA_POCAO_VIDA} HP.")
                selectedInventoryItem = Math.max(0, Math.min(selectedInventoryItem, inventario.items.size() - 1))
            }
            return
        }

        inventoryActionLoading = true
        registrarMensagemCombate("Usando ${item.nome}...")

        Thread.start("usar-pocao-vida") {
            try {
                Map batalha = ServiceRegistry.combateService.usarPocaoVida(currentBattleId)
                aplicarEstadoBatalhaBackend(batalha)
                inventario.removeItem(item.nome, 1)
                batalhasComPocaoUsada.add(currentBattleId)
                selectedInventoryItem = Math.max(0, Math.min(selectedInventoryItem, inventario.items.size() - 1))
                registrarMensagemCombate(batalha.mensagem ?: "${item.nome} usada! +${inventario.CURA_POCAO_VIDA} HP.")
            } catch (Exception e) {
                println "Erro ao usar pocao de vida: ${e.message}"
                pausarCombatePorErro("pocao")
            } finally {
                inventoryActionLoading = false
            }
        }
    }

    static void aplicarEstadoBatalhaBackend(Map batalha) {
        if (!batalha) return

        if (batalha.jogadorVidaAtual != null) {
            playerHP = (batalha.jogadorVidaAtual as Number).intValue()
        }
        if (batalha.jogadorManaAtual != null) {
            playerMana = (batalha.jogadorManaAtual as Number).intValue()
        }

        List inimigosBackend = (batalha.inimigos ?: []) as List
        for (int i = 0; i < 3; i++) {
            Map inimigo = inimigoBackendParaSlot(inimigosBackend, i)
            if (inimigo != null) {
                enemyIds[i] = inimigo.id == null ? null : inimigo.id as Long
                enemyNames[i] = inimigo.nome ?: enemyNames[i]
                enemyHP[i] = valorInteiro(inimigo.vidaAtual, enemyHP[i])
                enemyMaxHP[i] = valorInteiro(inimigo.vidaMaxima, enemyMaxHP[i])
                if (enemyHP[i] <= 0 || inimigo.vivo == false) {
                    enemyHP[i] = 0
                    enemyVisualState[i] = STATE_DEAD
                }
            } else {
                enemyIds[i] = null
                enemyHP[i] = 0
                enemyMaxHP[i] = 0
                enemyVisualState[i] = STATE_DEAD
            }
        }

        maxEnemyHP = Math.max(1, enemyMaxHP.max() as int)
    }

    static Map inimigoBackendParaSlot(List inimigosBackend, int slot) {
        if (!inimigosBackend || slot < 0) {
            return null
        }

        Long idAtual = slot < enemyIds.length ? enemyIds[slot] : null
        if (idAtual != null) {
            Map porId = inimigosBackend.find { inimigo ->
                def idBackend = (inimigo as Map).id
                idBackend != null && (idBackend as Long) == idAtual
            } as Map

            if (porId != null) {
                return porId
            }
        }

        slot < inimigosBackend.size() ? inimigosBackend[slot] as Map : null
    }

    static String nomeInimigoBackend(Map batalha, int index, String fallback) {
        List inimigosBackend = (batalha?.inimigos ?: []) as List
        if (index >= inimigosBackend.size()) return fallback
        Map inimigo = inimigosBackend[index] as Map
        inimigo.nome ?: fallback
    }

    static int vidaAtualInimigoBackend(Map batalha, int index, int fallback) {
        List inimigosBackend = (batalha?.inimigos ?: []) as List
        if (index >= inimigosBackend.size()) return fallback
        valorInteiro((inimigosBackend[index] as Map).vidaAtual, fallback)
    }

    static int vidaMaximaInimigoBackend(Map batalha, int index, int fallback) {
        List inimigosBackend = (batalha?.inimigos ?: []) as List
        if (index >= inimigosBackend.size()) return fallback
        valorInteiro((inimigosBackend[index] as Map).vidaMaxima, fallback)
    }

    static Long idInimigoBackend(Map batalha, int index) {
        List inimigosBackend = (batalha?.inimigos ?: []) as List
        if (index >= inimigosBackend.size()) return null
        def id = (inimigosBackend[index] as Map).id
        id == null ? null : id as Long
    }

    static int valorInteiro(def valor, int fallback) {
        valor == null ? fallback : (valor as Number).intValue()
    }

    static String pastaSpriteInimigo(String nome) {
        String normalizado = (nome ?: "").toLowerCase()
        if (normalizado.contains("minotauro")) return "Minotauro"
        if (normalizado.contains("wolf")) return "Wolfman"
        if (normalizado.contains("gorgon")) return "Gorgon dark"
        if (normalizado.contains("medusa")) return "medusa"
        if (normalizado.contains("warrior")) return "esqueleto warrior"
        if (normalizado.contains("esqueleto")) return "esqueleto"
        "esqueleto"
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
        game.render.GameRenderer.renderInventario(g)
    }
    static void renderitem(Graphics2D g) {
        game.render.GameRenderer.renderitem(g)
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
    static void renderEsqueceuSenha(Graphics2D g) {
        game.render.GameRenderer.renderEsqueceuSenha(g)
    }

}

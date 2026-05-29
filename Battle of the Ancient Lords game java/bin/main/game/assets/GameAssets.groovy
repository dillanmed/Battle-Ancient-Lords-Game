package game.assets

import game.Main

import javax.imageio.ImageIO
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import java.io.InputStream
import java.util.ArrayList
import java.util.Arrays
import java.util.List

class GameAssets extends Main {

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
}
package game.services

import java.awt.Font
import java.awt.GraphicsEnvironment

class FontService {
    private Font gameFontBase
    private final Class resourceOwner

    FontService(Class resourceOwner) {
        this.resourceOwner = resourceOwner
    }

    void load(String resourcePath = '/fonte/Pixel Letras.ttf') {
        try {
            InputStream input = resourceOwner.getResourceAsStream(resourcePath)
            File fontFile = new File("src/main/resources${resourcePath}")

            if (input != null) {
                gameFontBase = Font.createFont(Font.TRUETYPE_FONT, input)
            } else if (fontFile.exists()) {
                gameFontBase = Font.createFont(Font.TRUETYPE_FONT, fontFile)
            } else {
                gameFontBase = new Font('SansSerif', Font.BOLD, 12)
                return
            }

            GraphicsEnvironment.localGraphicsEnvironment.registerFont(gameFontBase)
        } catch (Exception ignored) {
            gameFontBase = new Font('SansSerif', Font.BOLD, 12)
        }
    }

    Font get(int style, float size) {
        gameFontBase != null ? gameFontBase.deriveFont(style, size) : new Font('SansSerif', style, (int) size)
    }
}

package game.services

import javax.imageio.ImageIO
import java.awt.image.BufferedImage

class AssetLoader {
    static BufferedImage image(Class resourceOwner, String path) {
        try {
            InputStream input = resourceOwner.getResourceAsStream(path)
            if (input != null) {
                return ImageIO.read(input)
            }

            File file = new File(path)
            file.exists() ? ImageIO.read(file) : null
        } catch (Exception ignored) {
            null
        }
    }
}

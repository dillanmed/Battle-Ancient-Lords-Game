package game.components

import java.awt.Graphics2D
import java.awt.Point
import java.awt.Rectangle
import java.awt.image.BufferedImage

class Button {
    Rectangle bounds
    BufferedImage image
    Runnable action

    boolean contains(Point point) {
        bounds?.contains(point)
    }

    void render(Graphics2D g) {
        if (image != null && bounds != null) {
            g.drawImage(image, bounds.x, bounds.y, bounds.width, bounds.height, null)
        }
    }

    void click() {
        action?.run()
    }
}

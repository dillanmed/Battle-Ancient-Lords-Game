package game.components

import java.awt.Color
import java.awt.Graphics2D
import java.awt.Rectangle

class Card {
    Rectangle bounds
    Color background = new Color(25, 25, 35, 220)
    Color border = new Color(130, 110, 75)

    void render(Graphics2D g) {
        if (bounds == null) {
            return
        }

        g.color = background
        g.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 8, 8)
        g.color = border
        g.drawRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 8, 8)
    }
}

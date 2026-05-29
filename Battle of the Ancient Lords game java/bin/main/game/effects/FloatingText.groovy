package game.effects

import java.awt.Color

class FloatingText {
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

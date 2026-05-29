package game.components

class Modal {
    boolean visible
    String message = ''

    void show(String message) {
        this.message = message
        visible = true
    }

    void hide() {
        visible = false
    }
}

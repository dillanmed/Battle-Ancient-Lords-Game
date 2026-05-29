package game.combate

class BarraVida {
    static int larguraAtual(int atual, int maximo, int larguraTotal) {
        if (maximo <= 0) {
            return 0
        }

        Math.max(0, (int) ((atual / (double) maximo) * larguraTotal))
    }
}

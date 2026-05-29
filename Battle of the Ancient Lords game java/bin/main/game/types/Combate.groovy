package game.types

class Combate {
    String id
    Personagem jogador
    List<Personagem> monstros = []
    int roundAtual = 1
    boolean turnoJogador = true
    List<String> log = []
}

package game.types

class Combate {
    String id
    Long batalhaId
    String status
    String resultado
    String turnoAtual
    Personagem jogador
    List<Personagem> monstros = []
    int roundAtual = 1
    boolean turnoJogador = true
    List<String> log = []
}

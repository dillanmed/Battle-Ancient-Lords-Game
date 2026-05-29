package game.personagens

class Monstros {
    static final List<String> NOMES_BASE = ['Inimigo 1', 'Inimigo 2', 'Inimigo 3']

    static List<game.types.Personagem> criarGrupoPadrao(int hp = 40) {
        NOMES_BASE.collect { nome ->
            new game.types.Personagem(nome: nome, classe: 'MONSTRO', hp: hp, maxHp: hp)
        }
    }
}

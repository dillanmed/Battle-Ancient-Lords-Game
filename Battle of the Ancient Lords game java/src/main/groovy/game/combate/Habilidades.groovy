package game.combate

import game.types.Habilidade

class Habilidades {
    static final Habilidade GOLPE_BASICO = new Habilidade(
            id: 'golpe-basico',
            nome: 'Golpe Basico',
            descricao: 'Ataque padrao',
            custoMana: 0,
            danoBase: 10
    )
}

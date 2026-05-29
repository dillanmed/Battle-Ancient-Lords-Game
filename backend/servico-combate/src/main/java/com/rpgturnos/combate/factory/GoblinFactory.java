package com.rpgturnos.combate.factory;

import com.rpgturnos.combate.model.CombatenteSnapshot;

public class GoblinFactory implements InimigoFactory {

    @Override
    public CombatenteSnapshot criar() {
        return CombatenteSnapshot.builder()
                .nome("Goblin")
                .tipo("INIMIGO")
                .vidaMaxima(80)
                .vidaAtual(80)
                .manaMaxima(20)
                .manaAtual(20)
                .ataque(15)
                .defesa(5)
                .nivel(1)
                .build();
    }
}

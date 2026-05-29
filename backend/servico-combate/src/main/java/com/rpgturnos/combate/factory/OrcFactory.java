package com.rpgturnos.combate.factory;

import com.rpgturnos.combate.model.CombatenteSnapshot;

public class OrcFactory implements InimigoFactory {

    @Override
    public CombatenteSnapshot criar() {
        return CombatenteSnapshot.builder()
                .nome("Orc")
                .tipo("INIMIGO")
                .vidaMaxima(120)
                .vidaAtual(120)
                .manaMaxima(10)
                .manaAtual(10)
                .ataque(22)
                .defesa(12)
                .nivel(2)
                .build();
    }
}

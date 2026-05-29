package com.rpgturnos.combate.factory;

import com.rpgturnos.combate.model.CombatenteSnapshot;

public class EsqueletoFactory implements InimigoFactory {

    @Override
    public CombatenteSnapshot criar() {
        return CombatenteSnapshot.builder()
                .nome("Esqueleto")
                .tipo("INIMIGO")
                .vidaMaxima(70)
                .vidaAtual(70)
                .manaMaxima(15)
                .manaAtual(15)
                .ataque(18)
                .defesa(7)
                .nivel(1)
                .build();
    }
}

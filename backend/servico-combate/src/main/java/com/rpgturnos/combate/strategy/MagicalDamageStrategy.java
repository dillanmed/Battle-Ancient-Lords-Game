package com.rpgturnos.combate.strategy;

import com.rpgturnos.combate.model.CombatenteSnapshot;

public class MagicalDamageStrategy implements DamageStrategy {

    @Override
    public int calcularDano(CombatenteSnapshot atacante, CombatenteSnapshot defensor) {
        return Math.max(atacante.getAtaque() + atacante.getNivel() - defensor.getDefesa() / 2, 1);
    }
}

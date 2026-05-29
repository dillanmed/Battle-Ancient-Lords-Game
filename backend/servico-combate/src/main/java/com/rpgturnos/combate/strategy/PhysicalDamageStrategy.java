package com.rpgturnos.combate.strategy;

import com.rpgturnos.combate.model.CombatenteSnapshot;

public class PhysicalDamageStrategy implements DamageStrategy {

    @Override
    public int calcularDano(CombatenteSnapshot atacante, CombatenteSnapshot defensor) {
        return Math.max(atacante.getAtaque() - defensor.getDefesa(), 1);
    }
}

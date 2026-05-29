package com.rpgturnos.combate.strategy;

import com.rpgturnos.combate.model.CombatenteSnapshot;

public class CriticalDamageStrategy implements DamageStrategy {

    private final DamageStrategy baseStrategy;

    public CriticalDamageStrategy(DamageStrategy baseStrategy) {
        this.baseStrategy = baseStrategy;
    }

    @Override
    public int calcularDano(CombatenteSnapshot atacante, CombatenteSnapshot defensor) {
        return baseStrategy.calcularDano(atacante, defensor) * 2;
    }
}

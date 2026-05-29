package com.rpgturnos.combate.strategy;

import com.rpgturnos.combate.model.CombatenteSnapshot;

public interface DamageStrategy {

    int calcularDano(CombatenteSnapshot atacante, CombatenteSnapshot defensor);
}

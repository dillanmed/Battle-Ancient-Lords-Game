package com.rpgturnos.combate.decorator;

import com.rpgturnos.combate.model.CombatenteSnapshot;

public abstract class CombatenteDecorator {

    protected final CombatenteSnapshot combatente;

    protected CombatenteDecorator(CombatenteSnapshot combatente) {
        this.combatente = combatente;
    }

    public abstract void aplicar();
}

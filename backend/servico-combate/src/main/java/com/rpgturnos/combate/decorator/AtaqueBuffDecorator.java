package com.rpgturnos.combate.decorator;

import com.rpgturnos.combate.model.CombatenteSnapshot;

public class AtaqueBuffDecorator extends CombatenteDecorator {

    private final int bonus;

    public AtaqueBuffDecorator(CombatenteSnapshot combatente, int bonus) {
        super(combatente);
        this.bonus = bonus;
    }

    @Override
    public void aplicar() {
        combatente.setAtaque(combatente.getAtaque() + bonus);
    }
}

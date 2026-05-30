package com.rpgturnos.combate.decorator;

import com.rpgturnos.combate.model.CombatenteSnapshot;

public class DefesaBuffDecorator extends CombatenteDecorator {

    private final int bonus;

    public DefesaBuffDecorator(CombatenteSnapshot combatente, int bonus) {
        super(combatente);
        this.bonus = bonus;
    }

    @Override
    public void aplicar() {
        combatente.setDefesa(combatente.getDefesa() + bonus);
    }
}

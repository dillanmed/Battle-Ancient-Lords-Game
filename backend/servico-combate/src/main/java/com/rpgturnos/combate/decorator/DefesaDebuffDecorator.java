package com.rpgturnos.combate.decorator;

import com.rpgturnos.combate.model.CombatenteSnapshot;

public class DefesaDebuffDecorator extends CombatenteDecorator {

    private final int reducao;

    public DefesaDebuffDecorator(CombatenteSnapshot combatente, int reducao) {
        super(combatente);
        this.reducao = reducao;
    }

    @Override
    public void aplicar() {
        combatente.setDefesa(Math.max(combatente.getDefesa() - reducao, 0));
    }
}

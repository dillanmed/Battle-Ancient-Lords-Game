package game.personagens

/**
 * Personagem do jogo - representa um personagem jogável
 * Esta classe estende a classe de tipo básica com métodos de lógica de jogo
 */
class Personagem extends game.types.Personagem {

    /**
     * Aplica dano ao personagem
     * @param dano Valor de dano a aplicar
     * @return HP restante após dano
     */
    int sofrerDano(int dano) {
        this.hp = Math.max(0, this.hp - dano)
        return this.hp
    }

    /**
     * Recupera vida do personagem
     * @param quantidade Quantidade de HP a recuperar
     * @return HP após recuperação
     */
    int recuperarVida(int quantidade) {
        this.hp = Math.min(this.maxHp, this.hp + quantidade)
        return this.hp
    }

    /**
     * Recupera mana do personagem
     * @param quantidade Quantidade de mana a recuperar
     * @return Mana após recuperação
     */
    int recuperarMana(int quantidade) {
        this.mana = Math.min(this.maxMana, this.mana + quantidade)
        return this.mana
    }

    /**
     * Gasta mana para realizar uma ação
     * @param quantidade Quantidade de mana a gastar
     * @return true se havia mana suficiente, false caso contrário
     */
    boolean gastarMana(int quantidade) {
        if (this.mana >= quantidade) {
            this.mana -= quantidade
            return true
        }
        return false
    }

    /**
     * Verifica se o personagem está vivo
     * @return true se HP > 0
     */
    boolean estaVivo() {
        this.hp > 0
    }

    /**
     * Verifica se o personagem está morto
     * @return true se HP <= 0
     */
    boolean estaMorto() {
        this.hp <= 0
    }

    /**
     * Calcula o dano a ser infligido considerando o bônus
     * @return Dano calculado (valor base + bônus)
     */
    int calcularDano() {
        Random random = new Random()
        int danoBase = 10 + random.nextInt(15)
        return danoBase + this.bonusDano
    }

    /**
     * Adiciona XP ao personagem
     * @param quantidade Quantidade de XP a adicionar
     * @return true se houve level up
     */
    boolean adicionarXP(int quantidade) {
        this.xp += quantidade
        int xpProximoLevel = 100 * this.nivel
        
        if (this.xp >= xpProximoLevel) {
            subirNivel()
            return true
        }
        return false
    }

    /**
     * Sobe um nível do personagem
     */
    private void subirNivel() {
        this.nivel++
        this.maxHp += 10
        this.hp = this.maxHp
        this.maxMana += 5
        this.mana = this.maxMana
        this.bonusDano += 2
    }

    /**
     * Obtém a porcentagem de HP
     * @return Porcentagem de HP (0-100)
     */
    int getPercentualHP() {
        ((this.hp / this.maxHp.toFloat()) * 100).toInteger()
    }

    /**
     * Obtém a porcentagem de mana
     * @return Porcentagem de mana (0-100)
     */
    int getPercentualMana() {
        ((this.mana / this.maxMana.toFloat()) * 100).toInteger()
    }

    /**
     * Obtém uma descrição textual do personagem
     * @return String descrevendo o personagem
     */
    String getDescricao() {
        "$nome (${classe}) Nível $nivel - HP: $hp/$maxHp - Mana: $mana/$maxMana"
    }

    /**
     * Converte o personagem em Map
     * @return Map com todos os atributos
     */
    Map toMap() {
        [
            id: this.id,
            nome: this.nome,
            classe: this.classe,
            hp: this.hp,
            maxHp: this.maxHp,
            mana: this.mana,
            maxMana: this.maxMana,
            nivel: this.nivel,
            xp: this.xp,
            bonusDano: this.bonusDano
        ]
    }
}

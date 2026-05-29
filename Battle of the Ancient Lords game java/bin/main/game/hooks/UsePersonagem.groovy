package game.hooks

import game.personagens.PersonagemService
import game.services.ServiceRegistry
import game.types.Personagem

/**
 * Hook para gerenciar personagens
 * Fornece métodos para criar, listar e atualizar personagens
 */
class UsePersonagem {
    final PersonagemService personagemService = ServiceRegistry.personagemService
    private Personagem personagemAtual = null

    /**
     * Lista todos os personagens do usuário
     * @return Lista de personagens
     */
    List<Personagem> listarPersonagens() {
        try {
            personagemService.listarPersonagens()
        } catch (Exception e) {
            println("Erro ao listar personagens: ${e.message}")
            return []
        }
    }

    /**
     * Cria um novo personagem
     * @param nome Nome do personagem
     * @param classe Classe do personagem (WARRIOR, ARCHER, MAGE, etc)
     * @return Personagem criado
     */
    Personagem criarPersonagem(String nome, String classe) {
        try {
            if (!nome?.trim() || !classe?.trim()) {
                println("Erro: Nome e classe são obrigatórios")
                return null
            }
            
            Personagem personagem = personagemService.criarPersonagem(nome, classe)
            personagemAtual = personagem
            return personagem
        } catch (Exception e) {
            println("Erro ao criar personagem: ${e.message}")
            return null
        }
    }

    /**
     * Obtém o personagem atual selecionado
     * @return Personagem atual ou null
     */
    Personagem getPersonagemAtual() {
        personagemAtual
    }

    /**
     * Seleciona um personagem
     * @param personagem Personagem a ser selecionado
     */
    void selecionarPersonagem(Personagem personagem) {
        if (personagem) {
            personagemAtual = personagem
        }
    }

    /**
     * Atualiza a vida do personagem
     * @param dano Dano a ser aplicado (positivo diminui HP, negativo aumenta HP)
     * @return HP restante após dano
     */
    int aplicarDano(int dano) {
        if (personagemAtual) {
            personagemAtual.hp = Math.max(0, personagemAtual.hp - dano)
            return personagemAtual.hp
        }
        return 0
    }

    /**
     * Recupera vida do personagem
     * @param quantidade Quantidade de HP a recuperar
     * @return HP após recuperação
     */
    int recuperarVida(int quantidade) {
        if (personagemAtual) {
            personagemAtual.hp = Math.min(personagemAtual.maxHp, personagemAtual.hp + quantidade)
            return personagemAtual.hp
        }
        return 0
    }

    /**
     * Recupera mana do personagem
     * @param quantidade Quantidade de mana a recuperar
     * @return Mana após recuperação
     */
    int recuperarMana(int quantidade) {
        if (personagemAtual) {
            personagemAtual.mana = Math.min(personagemAtual.maxMana, personagemAtual.mana + quantidade)
            return personagemAtual.mana
        }
        return 0
    }

    /**
     * Gasta mana do personagem
     * @param quantidade Quantidade de mana a gastar
     * @return true se havia mana suficiente, false caso contrário
     */
    boolean gastarMana(int quantidade) {
        if (personagemAtual && personagemAtual.mana >= quantidade) {
            personagemAtual.mana -= quantidade
            return true
        }
        return false
    }

    /**
     * Adiciona XP ao personagem
     * @param xpGanho XP a ser adicionado
     */
    void adicionarXP(int xpGanho) {
        if (personagemAtual) {
            personagemAtual.xp += xpGanho
            
            // Verifica level up
            if (personagemAtual.xp >= 100 * personagemAtual.nivel) {
                levelUp()
            }
        }
    }

    /**
     * Realiza level up do personagem
     */
    void levelUp() {
        if (personagemAtual) {
            personagemAtual.nivel++
            personagemAtual.maxHp += 10
            personagemAtual.hp = personagemAtual.maxHp
            personagemAtual.maxMana += 5
            personagemAtual.mana = personagemAtual.maxMana
            personagemAtual.bonusDano += 2
        }
    }

    /**
     * Verifica se o personagem está vivo
     * @return true se HP > 0
     */
    boolean estaVivo() {
        personagemAtual?.hp > 0
    }
}

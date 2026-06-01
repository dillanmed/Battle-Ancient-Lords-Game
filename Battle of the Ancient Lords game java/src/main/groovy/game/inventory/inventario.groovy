package game.inventory

class inventario {

    static final String POCAO_VIDA = "Pocao de Vida"
    static final int CURA_POCAO_VIDA = 25
    static List<Item> items = []

    static void addItem(String nome, String descricao, int quantidade) {
        Item itemExistente = items.find { it.nome == nome }

        if (itemExistente != null) {
            itemExistente.quantidade += quantidade
            println "[INVENTARIO] Quantidade atualizada: ${nome} x${itemExistente.quantidade}"
            return
        }

        items.add(new Item(nome, descricao, quantidade))
        println "[INVENTARIO] Novo item adicionado: ${nome}"
    }

    static void removeItem(String nome, int quantidade) {
        Item item = items.find { it.nome == nome }
        if (item == null) return

        item.quantidade -= quantidade
        if (item.quantidade <= 0) {
            items.remove(item)
            println "[INVENTARIO] Item removido: ${nome}"
        }
    }

    static boolean useItem(String nome) {
        Item item = items.find { it.nome == nome }
        if (item == null) {
            println "[INVENTARIO] Item nao encontrado."
            return false
        }

        if (nome != POCAO_VIDA) {
            println "[ITEM] Item sem efeito configurado."
            return false
        }

        if (game.Main.playerHP >= game.Main.maxHP) {
            println "[ITEM] HP ja esta cheio."
            return false
        }

        game.Main.playerHP = Math.min(game.Main.maxHP, game.Main.playerHP + CURA_POCAO_VIDA)
        removeItem(nome, 1)
        println "[ITEM] Pocao de Vida usada. +${CURA_POCAO_VIDA} HP."
        return true
    }

    static void printInventory() {
        println "\n========== INVENTARIO =========="

        if (items.isEmpty()) {
            println "Inventario vazio."
        } else {
            items.eachWithIndex { Item item, int index ->
                println "${index + 1}. ${item.nome} x${item.quantidade}"
            }
        }

        println "================================\n"
    }

    static void loadStarterItems() {
        items.clear()
        addItem(POCAO_VIDA, "Recupera ${CURA_POCAO_VIDA} de HP", 5)
    }
}

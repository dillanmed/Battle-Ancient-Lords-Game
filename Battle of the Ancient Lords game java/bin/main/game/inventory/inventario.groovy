package game.inventory

class inventario {

    // =========================================
    // LISTA DE ITENS
    // =========================================
    static List<Item> items = []

    // =========================================
    // ADICIONAR ITEM
    // =========================================
    static void addItem(String nome, String descricao, int quantidade) {

        Item itemExistente = items.find { it.nome == nome }

        if (itemExistente != null) {

            itemExistente.quantidade += quantidade

            println "[INVENTARIO] Quantidade atualizada: ${nome} x${itemExistente.quantidade}"

        } else {

            Item novoItem = new Item(
                    nome,
                    descricao,
                    quantidade
            )

            items.add(novoItem)

            println "[INVENTARIO] Novo item adicionado: ${nome}"
        }
    }

    // =========================================
    // REMOVER ITEM
    // =========================================
    static void removeItem(String nome, int quantidade) {

        Item item = items.find { it.nome == nome }

        if (item != null) {

            item.quantidade -= quantidade

            if (item.quantidade <= 0) {

                items.remove(item)

                println "[INVENTARIO] Item removido: ${nome}"
            }
        }
    }

    // =========================================
    // USAR ITEM
    // =========================================
    static void useItem(String nome) {

        Item item = items.find { it.nome == nome }

        if (item == null) {

            println "[INVENTARIO] Item não encontrado."
            return
        }

        println "[INVENTARIO] Usando item: ${nome}"

        switch (nome) {

            case "Pocao de Vida":

                game.Main.playerHP += 30

                if (game.Main.playerHP > game.Main.maxHP) {
                    game.Main.playerHP = game.Main.maxHP
                }

                println "[ITEM] Vida recuperada!"
                break

            case "Pocao de Mana":

                game.Main.playerMana += 25

                if (game.Main.playerMana > game.Main.maxMana) {
                    game.Main.playerMana = game.Main.maxMana
                }

                println "[ITEM] Mana recuperada!"
                break

            case "Elixir Supremo":

                game.Main.playerHP = game.Main.maxHP
                game.Main.playerMana = game.Main.maxMana

                println "[ITEM] HP e Mana restaurados!"
                break
        }

        removeItem(nome, 1)
    }

    // =========================================
    // PRINTAR INVENTARIO
    // =========================================
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

    // =========================================
    // ITENS INICIAIS
    // =========================================
    static void loadStarterItems() {

        addItem(
                "Pocao de Vida",
                "Recupera 30 de HP",
                5
        )

        addItem(
                "Pocao de Mana",
                "Recupera 25 de Mana",
                3
        )

        addItem(
                "Elixir Supremo",
                "Recupera HP e Mana completamente",
                1
        )
    }
}
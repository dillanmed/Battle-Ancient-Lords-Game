package game.inventory

class Item {

    String nome
    String descricao
    int quantidade

    Item(String nome, String descricao, int quantidade) {

        this.nome = nome
        this.descricao = descricao
        this.quantidade = quantidade
    }
}
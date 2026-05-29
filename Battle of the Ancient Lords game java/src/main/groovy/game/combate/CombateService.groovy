package game.combate

import game.services.ApiClient
import game.types.Combate
import game.types.Personagem

class CombateService {
    private final ApiClient apiClient

    CombateService(ApiClient apiClient) {
        this.apiClient = apiClient
    }

    Combate iniciar(Personagem jogador, List<Personagem> monstros) {
        new Combate(id: UUID.randomUUID().toString(), jogador: jogador, monstros: monstros, turnoJogador: true)
    }

    int calcularDano(Random random, int bonus = 0) {
        10 + bonus + random.nextInt(15)
    }
}

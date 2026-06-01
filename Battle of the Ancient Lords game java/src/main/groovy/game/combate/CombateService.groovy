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

    Map criarBatalha(Long personagemId, int fase) {
        apiClient.post('/batalhas', [
                personagemId: personagemId,
                fase: fase
        ])
    }

    Map iniciarBatalha(Number batalhaId) {
        apiClient.post("/batalhas/${batalhaId}/iniciar", [:])
    }

    Map buscarBatalha(Number batalhaId) {
        apiClient.get("/batalhas/${batalhaId}")
    }

    Map atacar(Number batalhaId, Number inimigoId) {
        apiClient.post("/batalhas/${batalhaId}/atacar", [
                inimigoId: inimigoId
        ])
    }

    Map usarHabilidade(Number batalhaId, Map habilidade) {
        apiClient.post("/batalhas/${batalhaId}/habilidade", habilidade)
    }

    Map usarPocaoVida(Number batalhaId) {
        apiClient.post("/batalhas/${batalhaId}/pocao-vida", [:])
    }

    Map criarEIniciarBatalha(Long personagemId, int fase) {
        Map batalha = criarBatalha(personagemId, fase)
        iniciarBatalha(batalha.id as Number)
    }

    int calcularDano(Random random, int bonus = 0) {
        10 + bonus + random.nextInt(15)
    }
}

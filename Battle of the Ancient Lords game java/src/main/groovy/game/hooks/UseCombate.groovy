package game.hooks

import game.combate.CombateService
import game.services.ServiceRegistry
import game.types.Combate
import game.types.Personagem

/**
 * Hook para gerenciar lógica de combate
 * Fornece métodos para iniciar combate, calcular dano, gerenciar rounds, etc
 */
class UseCombate {
    final CombateService combateService = ServiceRegistry.combateService
    private Combate combateAtual = null

    /**
     * Inicia um novo combate
     * @param jogador Personagem do jogador
     * @param monstros Lista de monstros inimigos
     * @return Combate criado
     */
    Combate iniciarCombate(Personagem jogador, List<Personagem> monstros) {
        combateAtual = combateService.iniciar(jogador, monstros)
        combateAtual
    }

    /**
     * Obtém o combate atual
     * @return Combate atual ou null se nenhum em andamento
     */
    Combate getCombateAtual() {
        combateAtual
    }

    /**
     * Calcula dano a ser infligido
     * @param bonus Bônus de dano
     * @return Valor de dano calculado
     */
    int calcularDano(int bonus = 0) {
        Random random = new Random()
        combateService.calcularDano(random, bonus)
    }

    /**
     * Toca turno do jogador
     * @return true se turno processado com sucesso
     */
    boolean processarTurnoJogador() {
        if (!combateAtual) {
            println("Erro: Nenhum combate ativo")
            return false
        }
        
        if (!combateAtual.turnoJogador) {
            println("Erro: Não é turno do jogador")
            return false
        }
        
        combateAtual.turnoJogador = false
        true
    }

    /**
     * Toca turno dos inimigos
     * @return true se turno processado com sucesso
     */
    boolean processarTurnoInimigos() {
        if (!combateAtual) {
            println("Erro: Nenhum combate ativo")
            return false
        }
        
        if (combateAtual.turnoJogador) {
            println("Erro: Não é turno dos inimigos")
            return false
        }
        
        combateAtual.turnoJogador = true
        true
    }

    /**
     * Verifica se combate está ativo
     * @return true se há combate ativo
     */
    boolean combateAtivo() {
        combateAtual != null
    }

    /**
     * Encerra o combate atual
     */
    void encerrarCombate() {
        combateAtual = null
    }

    /**
     * Adiciona mensagem ao log de combate
     * @param mensagem Mensagem a ser adicionada
     */
    void adicionarLogMensagem(String mensagem) {
        if (combateAtual) {
            combateAtual.log.add("[Round ${combateAtual.roundAtual}] $mensagem")
        }
    }

    /**
     * Obtém o log de combate
     * @return Lista de mensagens do log
     */
    List<String> getLogCombate() {
        combateAtual?.log ?: []
    }

    /**
     * Avança para próximo round
     */
    void avancarRound() {
        if (combateAtual) {
            combateAtual.roundAtual++
        }
    }
}

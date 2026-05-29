package game.personagens

import game.services.ApiClient
import game.types.Personagem

/**
 * Serviço responsável pela gestão de personagens
 * Integra com API de character-service e fornece cache local
 */
class PersonagemService {
    private final ApiClient apiClient
    private final List<Personagem> personagensCache = []
    private static final String PERSONAGENS_API_PATH = "/api/personagens"

    PersonagemService(ApiClient apiClient) {
        this.apiClient = apiClient
    }

    /**
     * Lista todos os personagens do usuário
     * @return Lista de personagens (do cache ou API)
     */
    List<Personagem> listarPersonagens() {
        try {
            if (personagensCache.isEmpty()) {
                // Tenta buscar da API
                Map resposta = apiClient.get(PERSONAGENS_API_PATH)
                if (resposta && resposta.personagens) {
                    personagensCache.addAll(resposta.personagens)
                }
            }
            return personagensCache
        } catch (Exception e) {
            println("Erro ao listar personagens: ${e.message}")
            // Retorna cache mesmo se API falhar
            return personagensCache
        }
    }

    /**
     * Cria um novo personagem
     * @param nome Nome do personagem
     * @param classe Classe do personagem
     * @return Personagem criado
     */
    Personagem criarPersonagem(String nome, String classe) {
        try {
            // Validações
            if (!nome?.trim()) throw new IllegalArgumentException("Nome é obrigatório")
            if (!classe?.trim()) throw new IllegalArgumentException("Classe é obrigatória")
            if (!validarClasse(classe)) throw new IllegalArgumentException("Classe inválida: $classe")

            // Define estatísticas base por classe
            Map statsBase = obterStatsBase(classe)

            // Cria o personagem
            Personagem personagem = new Personagem(
                id: UUID.randomUUID().toString(),
                nome: nome,
                classe: classe,
                hp: statsBase.hp,
                maxHp: statsBase.hp,
                mana: statsBase.mana,
                maxMana: statsBase.mana,
                bonusDano: statsBase.bonusDano,
                nivel: 1,
                xp: 0
            )

            // Tenta salvar na API
            try {
                Map params = [
                    nome: personagem.nome,
                    classe: personagem.classe,
                    hp: personagem.hp,
                    maxHp: personagem.maxHp,
                    mana: personagem.mana,
                    maxMana: personagem.maxMana
                ]
                Map resposta = apiClient.post(PERSONAGENS_API_PATH, params)
                if (resposta && resposta.id) {
                    personagem.id = resposta.id
                }
            } catch (Exception e) {
                println("Aviso: Não foi possível sincronizar com API: ${e.message}")
            }

            // Adiciona ao cache
            personagensCache.add(personagem)
            return personagem
        } catch (Exception e) {
            println("Erro ao criar personagem: ${e.message}")
            return null
        }
    }

    /**
     * Obtém um personagem pelo ID
     * @param id ID do personagem
     * @return Personagem encontrado ou null
     */
    Personagem obterPersonagem(String id) {
        try {
            // Procura no cache primeiro
            Personagem encontrado = personagensCache.find { it.id == id }
            if (encontrado) return encontrado

            // Tenta buscar da API
            Map resposta = apiClient.get("$PERSONAGENS_API_PATH/$id")
            if (resposta && resposta.id) {
                Personagem personagem = new Personagem(resposta)
                personagensCache.add(personagem)
                return personagem
            }
            return null
        } catch (Exception e) {
            println("Erro ao obter personagem: ${e.message}")
            return null
        }
    }

    /**
     * Atualiza um personagem
     * @param personagem Personagem a ser atualizado
     * @return true se atualizado com sucesso
     */
    boolean atualizarPersonagem(Personagem personagem) {
        try {
            if (!personagem?.id) {
                throw new IllegalArgumentException("ID do personagem é obrigatório")
            }

            // Tenta atualizar na API
            try {
                Map params = [
                    nome: personagem.nome,
                    classe: personagem.classe,
                    hp: personagem.hp,
                    maxHp: personagem.maxHp,
                    mana: personagem.mana,
                    maxMana: personagem.maxMana,
                    nivel: personagem.nivel,
                    xp: personagem.xp,
                    bonusDano: personagem.bonusDano
                ]
                apiClient.put("$PERSONAGENS_API_PATH/${personagem.id}", params)
            } catch (Exception e) {
                println("Aviso: Não foi possível sincronizar com API: ${e.message}")
            }

            // Atualiza no cache
            int indice = personagensCache.findIndexOf { it.id == personagem.id }
            if (indice >= 0) {
                personagensCache[indice] = personagem
            }
            return true
        } catch (Exception e) {
            println("Erro ao atualizar personagem: ${e.message}")
            return false
        }
    }

    /**
     * Deleta um personagem
     * @param id ID do personagem
     * @return true se deletado com sucesso
     */
    boolean deletarPersonagem(String id) {
        try {
            // Tenta deletar na API
            try {
                apiClient.delete("$PERSONAGENS_API_PATH/$id")
            } catch (Exception e) {
                println("Aviso: Não foi possível sincronizar com API: ${e.message}")
            }

            // Remove do cache
            personagensCache.removeIf { it.id == id }
            return true
        } catch (Exception e) {
            println("Erro ao deletar personagem: ${e.message}")
            return false
        }
    }

    /**
     * Obtém as estatísticas base de uma classe
     * @param classe Classe do personagem
     * @return Map com hp, mana e bonusDano base
     */
    private Map obterStatsBase(String classe) {
        switch (classe.toUpperCase()) {
            case 'WARRIOR':
                return [hp: 150, mana: 30, bonusDano: 10]
            case 'ARCHER':
                return [hp: 100, mana: 40, bonusDano: 8]
            case 'MAGE':
                return [hp: 80, mana: 80, bonusDano: 6]
            case 'PALADIN':
                return [hp: 130, mana: 50, bonusDano: 8]
            default:
                return [hp: 100, mana: 50, bonusDano: 5]
        }
    }

    /**
     * Valida se uma classe é válida
     * @param classe Classe a validar
     * @return true se válida
     */
    private boolean validarClasse(String classe) {
        ['WARRIOR', 'ARCHER', 'MAGE', 'PALADIN'].contains(classe.toUpperCase())
    }

    /**
     * Limpa o cache de personagens
     */
    void limparCache() {
        personagensCache.clear()
    }
}

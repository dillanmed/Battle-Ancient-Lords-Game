package game.personagens

import game.services.ApiClient
import game.types.Personagem

/**
 * Servico responsavel pela gestao de personagens.
 * Integra com o servico-personagem e mantem fallback local para o jogo continuar offline.
 */
class PersonagemService {
    private final ApiClient apiClient
    private final List<Personagem> personagensCache = []
    private static final String PERSONAGENS_API_PATH = "/personagens"

    PersonagemService(ApiClient apiClient) {
        this.apiClient = apiClient
    }

    List<Personagem> listarPersonagens() {
        listarPersonagensPorUsuario(1L)
    }

    List<Personagem> listarPersonagensPorUsuario(Long usuarioId) {
        try {
            if (personagensCache.isEmpty()) {
                Map resposta = apiClient.get("/usuarios/${usuarioId}/personagens")
                List dados = resposta?.data instanceof List ? resposta.data : resposta?.personagens
                if (dados) {
                    personagensCache.addAll(dados.collect { fromBackend(it as Map) })
                }
            }
            return personagensCache
        } catch (Exception e) {
            println("Erro ao listar personagens: ${e.message}")
            return personagensCache
        }
    }

    Personagem criarPersonagem(String nome, String classe) {
        criarPersonagem(1L, nome, classe)
    }

    Personagem criarPersonagem(Long usuarioId, String nome, String classe) {
        try {
            if (!nome?.trim()) throw new IllegalArgumentException("Nome e obrigatorio")
            if (!classe?.trim()) throw new IllegalArgumentException("Classe e obrigatoria")
            if (!validarClasse(classe)) throw new IllegalArgumentException("Classe invalida: $classe")

            try {
                println("Tentando criar personagem no backend...")
                Map resposta = apiClient.post(PERSONAGENS_API_PATH, [
                        usuarioId: usuarioId,
                        nome: nome,
                        classe: classe
                ])

                if (resposta && resposta.id) {
                    Personagem personagemBackend = fromBackend(resposta)
                    personagensCache.add(personagemBackend)
                    println("Personagem criado no backend com sucesso")
                    return personagemBackend
                }
            } catch (Exception e) {
                println("Falha ao criar personagem no backend. Usando fallback local. Motivo: ${e.message}")
            }

            Personagem personagemLocal = criarPersonagemLocal(nome, classe)
            personagensCache.add(personagemLocal)
            return personagemLocal
        } catch (Exception e) {
            println("Erro ao criar personagem: ${e.message}")
            return null
        }
    }

    Personagem obterPersonagem(String id) {
        buscarPersonagem(id)
    }

    Personagem buscarPersonagem(String id) {
        try {
            Personagem encontrado = personagensCache.find { it.id == id }
            if (encontrado) return encontrado

            Map resposta = apiClient.get("$PERSONAGENS_API_PATH/$id")
            if (resposta && resposta.id) {
                Personagem personagem = fromBackend(resposta)
                personagensCache.add(personagem)
                return personagem
            }
            return null
        } catch (Exception e) {
            println("Erro ao obter personagem: ${e.message}")
            return null
        }
    }

    Map buscarDadosCombate(String id) {
        try {
            apiClient.get("$PERSONAGENS_API_PATH/$id/dados-combate")
        } catch (Exception e) {
            println("Erro ao buscar dados de combate: ${e.message}")
            [:]
        }
    }

    List buscarHabilidadesDoPersonagem(String id) {
        try {
            Map resposta = apiClient.get("$PERSONAGENS_API_PATH/$id/habilidades")
            resposta?.data instanceof List ? resposta.data : []
        } catch (Exception e) {
            println("Erro ao buscar habilidades do personagem: ${e.message}")
            []
        }
    }

    boolean atualizarPersonagem(Personagem personagem) {
        try {
            if (!personagem?.id) {
                throw new IllegalArgumentException("ID do personagem e obrigatorio")
            }

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

    boolean deletarPersonagem(String id) {
        try {
            try {
                apiClient.delete("$PERSONAGENS_API_PATH/$id")
            } catch (Exception e) {
                println("Aviso: Nao foi possivel sincronizar com API: ${e.message}")
            }

            personagensCache.removeIf { it.id == id }
            return true
        } catch (Exception e) {
            println("Erro ao deletar personagem: ${e.message}")
            return false
        }
    }

    void limparCache() {
        personagensCache.clear()
    }

    private Personagem criarPersonagemLocal(String nome, String classe) {
        Map statsBase = obterStatsBase(classe)

        new Personagem(
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
    }

    private Personagem fromBackend(Map data) {
        int maxHp = valorInteiro(data.vidaMaxima, 100)
        int maxMana = valorInteiro(data.manaMaxima, 50)

        new Personagem(
                id: data.id?.toString(),
                nome: data.nome?.toString(),
                classe: classeParaFrontend(data.classe?.toString()),
                hp: maxHp,
                maxHp: maxHp,
                mana: maxMana,
                maxMana: maxMana,
                nivel: valorInteiro(data.nivel, 1),
                xp: valorInteiro(data.experiencia, 0),
                bonusDano: valorInteiro(data.ataque, 0)
        )
    }

    private String classeParaFrontend(String classe) {
        switch (classe?.toUpperCase()) {
            case 'GUERREIRO':
                return 'WARRIOR'
            case 'ARQUEIRO':
                return 'ARCHER'
            case 'MAGO':
                return 'MAGE'
            default:
                return classe
        }
    }

    private int valorInteiro(Object valor, int padrao) {
        valor == null ? padrao : valor as int
    }

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

    private boolean validarClasse(String classe) {
        ['WARRIOR', 'ARCHER', 'MAGE', 'PALADIN'].contains(classe.toUpperCase())
    }
}

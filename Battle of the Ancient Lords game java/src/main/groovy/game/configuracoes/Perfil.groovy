package game.configuracoes

import game.types.Usuario

/**
 * Componente para exibir e gerenciar o perfil do usuário
 * Responsável por exibir informações do usuário autenticado
 */
class Perfil {
    Usuario usuario

    /**
     * Obtém o nome do usuário
     * @return Nome do usuário
     */
    String getNome() {
        usuario?.nome ?: "Desconhecido"
    }

    /**
     * Obtém o email do usuário
     * @return Email do usuário
     */
    String getEmail() {
        usuario?.email ?: "sem-email@exemplo.com"
    }

    /**
     * Obtém o ID do usuário
     * @return ID do usuário
     */
    String getId() {
        usuario?.id ?: ""
    }

    /**
     * Atualiza o perfil do usuário
     * @param dados Map com os dados a atualizar (nome, email, etc)
     * @return true se atualizado com sucesso
     */
    boolean atualizar(Map dados) {
        try {
            if (!usuario) {
                throw new IllegalStateException("Nenhum usuário autenticado")
            }

            if (dados.nome) {
                usuario.nome = dados.nome
            }

            if (dados.email) {
                usuario.email = dados.email
            }

            return true
        } catch (Exception e) {
            println("Erro ao atualizar perfil: ${e.message}")
            return false
        }
    }

    /**
     * Verifica se há um usuário autenticado
     * @return true se há usuário
     */
    boolean temUsuario() {
        usuario != null
    }

    /**
     * Obtém todas as informações do perfil como Map
     * @return Map com todas as informações
     */
    Map toMap() {
        [
            id: getId(),
            nome: getNome(),
            email: getEmail(),
            token: usuario?.token ?: ""
        ]
    }
}

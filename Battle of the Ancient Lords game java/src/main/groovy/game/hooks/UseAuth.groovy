package game.hooks

import game.auth.AuthContext
import game.auth.AuthService
import game.services.ServiceRegistry
import game.types.Usuario

/**
 * Hook para gerenciar autenticação
 * Fornece métodos para login, cadastro, logout e verificação de estado de autenticação
 */
class UseAuth {
    final AuthService authService = ServiceRegistry.authService
    final AuthContext authContext = ServiceRegistry.authContext

    /**
     * Realiza login com email e senha
     * @param email Email do usuário
     * @param password Senha do usuário
     * @return Usuario se login bem-sucedido, null caso contrário
     */
    Usuario login(String email, String password) {
        try {
            Usuario usuario = authService.login(email, password)
            return usuario
        } catch (Exception e) {
            println("Erro ao fazer login: ${e.message}")
            return null
        }
    }

    /**
     * Realiza cadastro de novo usuário
     * @param nome Nome do usuário
     * @param email Email do usuário
     * @param password Senha do usuário
     * @return Usuario se cadastro bem-sucedido, null caso contrário
     */
    Usuario cadastrar(String nome, String email, String password) {
        try {
            if (!nome?.trim() || !email?.trim() || !password?.trim()) {
                println("Erro: Todos os campos são obrigatórios")
                return null
            }
            
            Usuario usuario = authService.cadastrar(nome, email, password)
            return usuario
        } catch (Exception e) {
            println("Erro ao cadastrar: ${e.message}")
            return null
        }
    }

    /**
     * Realiza logout do usuário
     */
    void logout() {
        authService.logout()
    }

    /**
     * Solicita recuperação de senha
     * @param email Email do usuário
     */
    void solicitarRecuperacaoSenha(String email) {
        try {
            authService.solicitarRecuperacaoSenha(email)
        } catch (Exception e) {
            println("Erro ao solicitar recuperação: ${e.message}")
        }
    }

    /**
     * Obtém o usuário autenticado atual
     * @return Usuario autenticado ou null
     */
    Usuario getUsuarioAtual() {
        authContext.usuarioAutenticado
    }

    /**
     * Verifica se o usuário está autenticado
     * @return true se autenticado, false caso contrário
     */
    boolean isAutenticado() {
        authContext.isAutenticado()
    }

    /**
     * Obtém o token de autenticação
     * @return Token ou null
     */
    String getToken() {
        authContext.token
    }
}

package game.auth

import game.services.ApiClient
import game.services.TokenHandler
import game.types.Usuario

class AuthService {
    private final ApiClient apiClient
    private final TokenHandler tokenHandler
    private final AuthContext authContext

    AuthService(ApiClient apiClient, TokenHandler tokenHandler, AuthContext authContext) {
        this.apiClient = apiClient
        this.tokenHandler = tokenHandler
        this.authContext = authContext
    }

    Usuario login(String username, String password) {
        if (username == 'admin' && password == '123') {
            Usuario usuario = new Usuario(id: 'local-admin', nome: 'admin', email: 'admin@local', token: 'local-token')
            tokenHandler.save(usuario.token)
            authContext.login(usuario)
            return usuario
        }

        return null
    }

    Usuario cadastrar(String nome, String email, String password) {
        Usuario usuario = new Usuario(id: UUID.randomUUID().toString(), nome: nome, email: email, token: 'local-token')
        tokenHandler.save(usuario.token)
        authContext.login(usuario)
        usuario
    }

    void solicitarRecuperacaoSenha(String email) {
        if (!email?.trim()) {
            throw new IllegalArgumentException('Informe um e-mail valido')
        }
    }

    void logout() {
        tokenHandler.clear()
        authContext.logout()
    }
}

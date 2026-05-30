package game.auth

import game.services.ApiClient
import game.services.ApiException
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
        Map response = apiClient.post('/api/auth/login', [
                email: username?.trim(),
                senha: password
        ])

        autenticarComResponse(response)
    }

    Usuario cadastrar(String nome, String email, String password) {
        Map response = apiClient.post('/api/auth/cadastro', [
                nome: nome?.trim(),
                email: email?.trim(),
                senha: password
        ])

        autenticarComResponse(response)
    }

    String solicitarRecuperacaoSenha(String email) {
        if (!email?.trim()) {
            throw new IllegalArgumentException('Informe um e-mail valido')
        }

        Map response = apiClient.post('/api/auth/esqueci-senha', [email: email.trim()])
        response.mensagem ?: 'Se o e-mail existir, enviaremos as instrucoes.'
    }

    Usuario carregarUsuarioAutenticado() {
        if (!tokenHandler.authenticated) {
            return null
        }

        Map response = apiClient.get('/api/auth/me')
        Usuario usuario = usuarioFromPerfil(response)
        authContext.login(usuario)
        usuario
    }

    void logout() {
        tokenHandler.clear()
        authContext.logout()
    }

    private Usuario autenticarComResponse(Map response) {
        String token = response.token as String
        if (!token) {
            throw new ApiException(500, 'Resposta de autenticacao sem token.', '')
        }

        tokenHandler.save(token)
        Usuario usuario = new Usuario(
                id: response.usuarioId?.toString(),
                nome: response.nome as String,
                email: response.email as String,
                token: token
        )
        authContext.login(usuario)
        usuario
    }

    private Usuario usuarioFromPerfil(Map response) {
        new Usuario(
                id: response.usuarioId?.toString(),
                nome: response.nome as String,
                email: response.email as String,
                token: tokenHandler.token
        )
    }
}

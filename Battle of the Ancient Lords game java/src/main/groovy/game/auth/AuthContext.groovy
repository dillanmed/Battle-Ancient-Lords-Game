package game.auth

import game.types.Usuario

class AuthContext {
    Usuario usuarioAtual

    boolean isLoggedIn() {
        usuarioAtual != null
    }

    void login(Usuario usuario) {
        usuarioAtual = usuario
    }

    void logout() {
        usuarioAtual = null
    }
}

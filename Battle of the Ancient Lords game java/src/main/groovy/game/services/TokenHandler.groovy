package game.services

class TokenHandler {
    private String token

    String getToken() {
        token
    }

    void save(String newToken) {
        token = newToken
    }

    void clear() {
        token = null
    }

    boolean isAuthenticated() {
        token != null && !token.trim().empty
    }
}

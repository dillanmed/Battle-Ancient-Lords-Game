package game.services

class TokenHandler {
    private final File tokenFile = new File(System.getProperty('user.home'), '.ancient-lords/auth-token')
    private String token = carregarToken()

    String getToken() {
        token
    }

    void save(String newToken) {
        token = newToken
        tokenFile.parentFile?.mkdirs()
        tokenFile.text = newToken ?: ''
    }

    void clear() {
        token = null
        if (tokenFile.exists()) {
            tokenFile.delete()
        }
    }

    boolean isAuthenticated() {
        token != null && !token.trim().empty
    }

    private String carregarToken() {
        tokenFile.exists() ? tokenFile.text?.trim() : null
    }
}

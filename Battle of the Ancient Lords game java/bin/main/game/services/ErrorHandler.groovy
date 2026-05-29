package game.services

class ErrorHandler {
    String humanize(Throwable error) {
        if (error == null) {
            return 'Erro desconhecido'
        }

        error.message ?: error.class.simpleName
    }
}

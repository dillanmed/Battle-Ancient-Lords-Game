package game.services

import game.auth.AuthContext
import game.auth.AuthService
import game.combate.CombateService
import game.personagens.PersonagemService

class ServiceRegistry {
    static final TokenHandler tokenHandler = new TokenHandler()
    static final ErrorHandler errorHandler = new ErrorHandler()
    static final AuthContext authContext = new AuthContext()

    static final AuthService authService = new AuthService(
            new ApiClient('http://localhost:8081', tokenHandler),
            tokenHandler,
            authContext
    )

    static final PersonagemService personagemService = new PersonagemService(
            new ApiClient('http://localhost:8081', tokenHandler)
    )

    static final CombateService combateService = new CombateService(
            new ApiClient('http://localhost:8083', tokenHandler)
    )
}

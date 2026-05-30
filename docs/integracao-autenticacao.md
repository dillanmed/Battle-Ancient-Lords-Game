# Integracao de autenticacao

## Singleton no backend

No Spring Boot, classes anotadas com `@Service` sao singleton por padrao. O container cria uma unica instancia de `AuthService` e injeta essa mesma instancia no `AuthController` e no `JwtAuthenticationFilter`.

```java
@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }
}
```

## Supabase

O projeto usa o Supabase como Postgres gerenciado via Spring Data JPA. Para rodar com Supabase:

```powershell
$env:SUPABASE_DB_URL="jdbc:postgresql://HOST:5432/postgres"
$env:SUPABASE_DB_USERNAME="postgres.PROJECT_REF"
$env:SUPABASE_DB_PASSWORD="SENHA_DO_BANCO"
$env:SPRING_PROFILES_ACTIVE="postgres"
```

A tabela principal e `usuarios`. O email e unico, a senha e salva com BCrypt e nunca deve ser persistida em texto puro.

## Endpoints

- `POST /api/auth/cadastro`

```json
{
  "login": "arthur",
  "email": "arthur@email.com",
  "senha": "123456"
}
```

Retorna `201 Created` com `token`. Se o email ja existir, retorna `409 Conflict`.

- `POST /api/auth/login`

```json
{
  "email": "arthur@email.com",
  "senha": "123456"
}
```

Tambem aceita o login no campo `email`. Retorna `200 OK` com `token`. Credenciais invalidas retornam `401 Unauthorized`.

- `POST /api/auth/esqueci-senha`

```json
{
  "email": "arthur@email.com"
}
```

Retorna uma mensagem generica para nao revelar se o email existe.

- `GET /api/auth/me`

Requer cabecalho:

```http
Authorization: Bearer TOKEN
```

Retorna os dados do usuario autenticado para exibir na tela.

## Frontend LitieEngine

O cliente Groovy centraliza chamadas em `ApiClient`. Quando existe token salvo, ele envia automaticamente:

```http
Authorization: Bearer <TOKEN>
```

`AuthService.groovy` consome `cadastro`, `login`, `esqueci-senha` e `me`. O token fica salvo em `%USERPROFILE%\.ancient-lords\auth-token`, equivalente ao LocalStorage para o cliente desktop.

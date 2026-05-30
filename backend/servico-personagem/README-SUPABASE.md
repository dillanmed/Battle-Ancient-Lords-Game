# Configuracao Supabase - servico-personagem

Este servico usa PostgreSQL via Supabase no profile Spring `supabase`.

A senha real do banco nao deve ser salva no repositorio. Use somente variavel de ambiente no terminal.

## Modo recomendado

O arquivo `src/main/resources/application-supabase.yaml` ja define:

- host do Supabase;
- porta `5432`;
- database `postgres`;
- usuario `postgres.egnbgiknsyzojqfddxyt`;
- `sslmode=require`;
- porta HTTP `8081`.

Assim, para rodar com Supabase, basta ativar o profile e informar a senha.

## Git Bash

```bash
unset SPRING_DATASOURCE_URL
unset SPRING_DATASOURCE_USERNAME
unset SPRING_DATASOURCE_PASSWORD

export SPRING_PROFILES_ACTIVE=supabase
export SUPABASE_DB_PASSWORD='SENHA_REAL_DO_BANCO'

./mvnw spring-boot:run
```

## PowerShell

```powershell
Remove-Item Env:SPRING_DATASOURCE_URL -ErrorAction SilentlyContinue
Remove-Item Env:SPRING_DATASOURCE_USERNAME -ErrorAction SilentlyContinue
Remove-Item Env:SPRING_DATASOURCE_PASSWORD -ErrorAction SilentlyContinue

$env:SPRING_PROFILES_ACTIVE="supabase"
$env:SUPABASE_DB_PASSWORD="SENHA_REAL_DO_BANCO"

.\mvnw.cmd spring-boot:run
```

## CMD

```cmd
set SPRING_DATASOURCE_URL=
set SPRING_DATASOURCE_USERNAME=
set SPRING_DATASOURCE_PASSWORD=

set SPRING_PROFILES_ACTIVE=supabase
set SUPABASE_DB_PASSWORD=SENHA_REAL_DO_BANCO

mvnw.cmd spring-boot:run
```

## Como confirmar se o profile correto esta ativo

Ao iniciar, o log deve mostrar algo equivalente a:

```text
The following 1 profile is active: "supabase"
```

Se aparecer:

```text
No active profile set, falling back to 1 default profile: "default"
```

entao o profile `supabase` nao foi ativado no terminal atual.

## Sinais de conexao correta

Com a senha correta, o log deve chegar a mensagens como:

```text
HikariPool-1 - Start completed
Tomcat started on port 8081
Started ServicoPersonagemApplication
```

## Se ainda ocorrer erro de autenticacao

Erro comum:

```text
FATAL: password authentication failed for user "postgres"
```

Verifique:

- se o profile ativo no log e `supabase`;
- se `SUPABASE_DB_PASSWORD` foi definida no mesmo terminal que executa o Maven;
- se a senha informada e a senha atual do banco no Supabase;
- se nao ha variaveis antigas `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` ou `SPRING_DATASOURCE_PASSWORD` sobrescrevendo a configuracao;
- se a URL usada contem `sslmode=require`.

Nunca imprima a senha no terminal. Para conferir apenas se ela existe, use o tamanho:

```bash
echo ${#SUPABASE_DB_PASSWORD}
```

## Validacao manual

Com o servico rodando na porta `8081`, validar:

- `GET http://localhost:8081/habilidades`
- `POST http://localhost:8081/personagens`
- `GET http://localhost:8081/personagens/1`
- `GET http://localhost:8081/personagens/1/dados-combate`
- `GET http://localhost:8081/personagens/1/habilidades`

Exemplo de corpo para criacao de personagem:

```json
{
  "usuarioId": 1,
  "nome": "Ayla",
  "classe": "ARCHER"
}
```

Tambem deve continuar aceitando:

```json
{
  "usuarioId": 1,
  "nome": "Ayla",
  "classe": "ARQUEIRO"
}
```

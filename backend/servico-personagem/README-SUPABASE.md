# Configuracao Supabase - servico-personagem

Este servico usa PostgreSQL via Supabase. A senha real nao deve ser salva no repositorio.

## Variaveis necessarias

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Git Bash

Modo recomendado: use URL, usuario e senha em variaveis separadas.

```bash
unset SPRING_DATASOURCE_URL
unset SPRING_DATASOURCE_USERNAME
unset SPRING_DATASOURCE_PASSWORD

export SPRING_DATASOURCE_URL='jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:5432/postgres?sslmode=require'
export SPRING_DATASOURCE_USERNAME='postgres.egnbgiknsyzojqfddxyt'
export SPRING_DATASOURCE_PASSWORD='SUA_SENHA_AQUI'

./mvnw spring-boot:run
```

Para conferir se as variaveis foram carregadas no mesmo terminal:

```bash
echo "$SPRING_DATASOURCE_URL"
echo "$SPRING_DATASOURCE_USERNAME"
echo ${#SPRING_DATASOURCE_PASSWORD}
```

O ultimo comando mostra apenas o tamanho da senha, nao o valor.

## PowerShell

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:5432/postgres?sslmode=require"
$env:SPRING_DATASOURCE_USERNAME="postgres.egnbgiknsyzojqfddxyt"
$env:SPRING_DATASOURCE_PASSWORD="SUA_SENHA_AQUI"

.\mvnw.cmd spring-boot:run
```

## CMD

```cmd
set SPRING_DATASOURCE_URL=jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:5432/postgres?sslmode=require
set SPRING_DATASOURCE_USERNAME=postgres.egnbgiknsyzojqfddxyt
set SPRING_DATASOURCE_PASSWORD=SUA_SENHA_AQUI

mvnw.cmd spring-boot:run
```

## Sobre a URL completa do Supabase

A connection string oficial do Supabase tambem pode vir no formato:

```text
jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:5432/postgres?user=postgres.egnbgiknsyzojqfddxyt&password=SUA_SENHA_AQUI&sslmode=require
```

Neste projeto, prefira as variaveis separadas (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`). Como o `application.yaml` define `spring.datasource.username` e `spring.datasource.password`, misturar senha/usuario dentro da URL pode causar confusao durante o diagnostico.

## Diagnostico de autenticacao

Se aparecer `FATAL: password authentication failed for user "postgres"`, isso nao significa necessariamente que o Spring usou `SPRING_DATASOURCE_USERNAME=postgres`.

No Supabase, o usuario do Session Pooler tem o formato `postgres.<project-ref>`, mas o papel interno do banco pode aparecer como `postgres` na mensagem de erro. Se o usuario pooler estiver errado, o erro tende a mencionar `tenant/user ... not found`.

Verifique estes pontos:

- a URL nao deve conter `?user=postgres` nem senha embutida;
- `SPRING_DATASOURCE_USERNAME` deve ser `postgres.egnbgiknsyzojqfddxyt`;
- `SPRING_DATASOURCE_PASSWORD` deve estar definida no mesmo terminal que executa o Maven;
- se a senha tiver caracteres especiais, prefira aspas simples no Git Bash;
- a URL recomendada e `jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:5432/postgres?sslmode=require`.

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

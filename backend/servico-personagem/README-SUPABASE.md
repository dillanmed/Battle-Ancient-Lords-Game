# Configuracao Supabase - servico-personagem

Este servico usa PostgreSQL via Supabase. A senha real nao deve ser salva no repositorio.

## Variaveis necessarias

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

## Git Bash

```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:5432/postgres"
export SPRING_DATASOURCE_USERNAME="postgres.egnbgiknsyzojqfddxyt"
export SPRING_DATASOURCE_PASSWORD="SUA_SENHA_AQUI"

./mvnw spring-boot:run
```

## PowerShell

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:5432/postgres"
$env:SPRING_DATASOURCE_USERNAME="postgres.egnbgiknsyzojqfddxyt"
$env:SPRING_DATASOURCE_PASSWORD="SUA_SENHA_AQUI"

.\mvnw.cmd spring-boot:run
```

## CMD

```cmd
set SPRING_DATASOURCE_URL=jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:5432/postgres
set SPRING_DATASOURCE_USERNAME=postgres.egnbgiknsyzojqfddxyt
set SPRING_DATASOURCE_PASSWORD=SUA_SENHA_AQUI

mvnw.cmd spring-boot:run
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

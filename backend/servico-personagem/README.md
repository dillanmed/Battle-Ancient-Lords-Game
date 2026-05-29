# servico-personagem

Microsservico responsavel por criacao, consulta, habilidades e progressao de personagens do RPG de turnos.

## Responsabilidades

- Criar personagens por usuario.
- Aplicar atributos iniciais por classe usando `PersonagemFactory`.
- Listar habilidades cadastradas e habilidades disponiveis por classe/nivel.
- Expor dados limpos para uso futuro pelo `servico-combate`.
- Adicionar experiencia e evoluir personagens.

Este servico nao implementa combate, autenticacao/JWT, inimigos, historico ou mensageria.

## Como rodar

Na pasta `backend/servico-personagem`:

```powershell
.\mvnw.cmd spring-boot:run
```

Por padrao, a aplicacao sobe em:

```text
http://localhost:8081
```

## Variaveis de ambiente

O banco PostgreSQL fica no Supabase. A senha real nao deve ser commitada.

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:5432/postgres?sslmode=require"
$env:SPRING_DATASOURCE_USERNAME="postgres.egnbgiknsyzojqfddxyt"
$env:SPRING_DATASOURCE_PASSWORD="SUA_SENHA_AQUI"
```

O `application.yaml` tambem aceita rodar sem senha configurada, mas a conexao real com Supabase so funciona quando `SPRING_DATASOURCE_PASSWORD` esta correta no terminal.

## Testes

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
```

Os testes usam profile `test` com H2 em memoria.

## Classes aceitas

Valores principais:

- `GUERREIRO`
- `MAGO`
- `ARQUEIRO`

Aliases aceitos:

- `WARRIOR` -> `GUERREIRO`
- `MAGE` -> `MAGO`
- `ARCHER` -> `ARQUEIRO`

## Endpoints

### Personagens

- `POST /personagens`
- `GET /personagens/{id}`
- `GET /usuarios/{usuarioId}/personagens`
- `GET /personagens/{id}/habilidades`
- `GET /personagens/{id}/dados-combate`
- `PUT /personagens/{id}/experiencia`
- `PUT /personagens/{id}/evoluir`
- `DELETE /personagens/{id}`

### Habilidades

- `GET /habilidades`
- `GET /habilidades/classe/{classe}`
- `GET /habilidades/{id}`

## Exemplos JSON

Criar personagem:

```json
{
  "usuarioId": 1,
  "nome": "Ayla",
  "classe": "ARCHER"
}
```

Adicionar experiencia:

```json
{
  "experiencia": 120
}
```

Exemplo de dados de combate:

```json
{
  "id": 1,
  "nome": "Ayla",
  "classe": "ARQUEIRO",
  "nivel": 1,
  "vidaMaxima": 90,
  "manaMaxima": 60,
  "ataque": 16,
  "defesa": 12,
  "forca": 12,
  "inteligencia": 10,
  "agilidade": 18,
  "habilidades": [
    {
      "id": 1,
      "nome": "Flecha Precisa",
      "descricao": "Disparo certeiro com alta chance de dano.",
      "classePermitida": "ARQUEIRO",
      "tipo": "ATAQUE",
      "custoMana": 10,
      "poder": 24,
      "nivelNecessario": 1
    }
  ]
}
```

Erro padrao:

```json
{
  "status": 404,
  "erro": "Personagem nao encontrado",
  "mensagem": "Personagem nao encontrado com id: 10"
}
```

## Regras de progressao

- Experiencia necessaria para evoluir: `nivel * 100`.
- Ao evoluir, a experiencia necessaria e consumida.
- O nivel aumenta em 1.
- Vida, mana, ataque e defesa recebem aumento base.
- Atributos principais aumentam conforme a classe:
  - GUERREIRO: mais vida, defesa e forca.
  - MAGO: mais mana, inteligencia e ataque.
  - ARQUEIRO: mais agilidade, forca e ataque.

## Integracao futura com servico-combate

O `servico-combate` deve consumir futuramente:

```text
GET /personagens/{id}/dados-combate
```

Esse endpoint retorna apenas os dados necessarios para combate por turnos, incluindo atributos e habilidades disponiveis para a classe e nivel do personagem.

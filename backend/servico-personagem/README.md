# servico-personagem

Microsservico responsavel por criacao, consulta, habilidades e progressao de personagens do RPG de turnos.

## Responsabilidades

- Criar personagens por usuario.
- Aplicar atributos iniciais por classe usando `PersonagemFactory`.
- Listar habilidades cadastradas e habilidades disponiveis por classe/nivel.
- Expor dados limpos para uso futuro pelo `servico-combate`.
- Adicionar experiencia e evoluir personagens.

Este servico nao implementa combate, autenticacao/JWT, inimigos, historico ou mensageria.

## Como rodar localmente

Na pasta `backend/servico-personagem`:

```powershell
.\mvnw.cmd spring-boot:run
```

Por padrao, a aplicacao sobe em:

```text
http://localhost:8081
```

Sem profile ativo, o servico usa a configuracao default com PostgreSQL local em `localhost:5432/db_personagem`.

## Rodar com Supabase

O banco PostgreSQL do Supabase deve ser usado com o profile `supabase`. A senha real nao deve ser commitada.

```powershell
$env:SPRING_PROFILES_ACTIVE="supabase"
$env:SUPABASE_DB_PASSWORD="SENHA_REAL_DO_BANCO"

.\mvnw.cmd spring-boot:run
```

O log deve indicar que o profile `supabase` esta ativo. Mais detalhes estao em `README-SUPABASE.md`.

## Testes

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
```

Os testes usam profile `test` com PostgreSQL configurado por variaveis de ambiente.

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

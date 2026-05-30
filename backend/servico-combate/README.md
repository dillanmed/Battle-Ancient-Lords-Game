# servico-combate

Microservico responsavel por criar e controlar batalhas em turnos do RPG. Ele nao e dono dos dados definitivos de personagem: para iniciar uma batalha, consulta o `character-service` e grava um `CombatenteSnapshot`, que representa o estado congelado do jogador durante aquela batalha.

## Arquitetura

Camadas principais:

- `controller`: endpoints REST de batalha.
- `facade`: ponto unico de entrada para operacoes de combate.
- `service`: regras de negocio de batalha, turno, dano, defesa, habilidade e eventos.
- `repository`: acesso JPA para `Batalha` e `EventoBatalha`.
- `model`: entidades, enums e snapshots persistidos.
- `client`: adapter HTTP para o `character-service`.
- `factory`, `strategy`, `state`, `decorator`: padroes de projeto usados no dominio.
- `aspect`: logs e tempo de execucao via AOP.

## Integracao com character-service

O combate busca dados reais do personagem em:

```http
GET /personagens/{id}/dados-combate
```

A URL base fica em `application.yaml`:

```yaml
services:
  character-service:
    url: http://localhost:8081
```

O contrato do `character-service` nao e alterado por este microservico.

## CombatenteSnapshot

`CombatenteSnapshot` existe para congelar os atributos usados na batalha, como vida, mana, ataque, defesa e nivel. Isso evita que uma mudanca posterior no personagem original altere uma batalha ja criada ou em andamento.

## Endpoints

```http
POST /batalhas
GET /batalhas
GET /batalhas/{id}
POST /batalhas/{id}/iniciar
POST /batalhas/{id}/atacar
POST /batalhas/{id}/defender
POST /batalhas/{id}/habilidade
POST /batalhas/{id}/finalizar
GET /batalhas/{id}/eventos
```

Exemplo para criar batalha:

```json
{
  "usuarioId": 1,
  "personagemId": 10
}
```

Exemplo para usar habilidade:

```json
{
  "nome": "Golpe Arcano",
  "tipo": "MAGICA",
  "dano": 10,
  "custoMana": 5
}
```

Tipos aceitos em habilidade:

- `DANO`, `FISICA`, `ATAQUE`
- `MAGICA`, `DANO_MAGICO`
- `BUFF_ATAQUE`
- `BUFF_DEFESA`
- `DEBUFF_DEFESA`

## Eventos Persistidos

O servico registra eventos para:

- batalha criada
- batalha iniciada
- ataque
- defesa
- habilidade usada
- turno alterado
- vitoria
- derrota
- batalha finalizada

Ao finalizar uma batalha, tambem publica evento RabbitMQ em `batalha.finalizada`. Caso o broker nao esteja disponivel, o erro e registrado em log sem quebrar a finalizacao da batalha.

## Design Patterns

- Factory: cria inimigos (`GoblinFactory`, `OrcFactory`, `EsqueletoFactory`).
- Builder: Lombok `@Builder` nas entidades/DTOs de resposta.
- Singleton: beans Spring padrao, como services, controllers, facade e aspects.
- Adapter: `CharacterClientImpl` adapta chamada HTTP ao `character-service`.
- Facade: `CombateFacade` centraliza operacoes de combate.
- Decorator: buffs/debuffs em `AtaqueBuffDecorator`, `DefesaBuffDecorator` e `DefesaDebuffDecorator`.
- Strategy: calculo de dano fisico/magico por `DamageStrategy`.
- State: classes `BatalhaState`, `BatalhaEmAndamentoState` e `BatalhaFinalizadaState`.
- Observer: publicacao de evento de batalha finalizada via RabbitMQ.

## SOLID

- SRP: controller, facade, service, repository, client e aspects possuem responsabilidades separadas.
- OCP: novas strategies, factories e decorators podem ser adicionados sem alterar toda a regra de combate.
- LSP: implementacoes de `DamageStrategy` e `InimigoFactory` podem substituir seus contratos.
- ISP: contratos pequenos como `CharacterClient`, `DamageStrategy` e `InimigoFactory`.
- DIP: services dependem de interfaces/beans em vez de detalhes diretos de infraestrutura sempre que aplicavel.

## GRASP

- Controller: `BatalhaController` recebe requisicoes REST.
- Creator: factories criam inimigos.
- Information Expert: `BatalhaService` concentra regras de turno e estado de batalha.
- Low Coupling: integracao externa fica isolada em `CharacterClient`.
- High Cohesion: eventos ficam em `EventoBatalhaService`, mapeamentos em `BatalhaMapper`.

## AOP

Foram implementados:

- `LoggingAspect`: registra metodo chamado e argumentos resumidos.
- `ExecutionTimeAspect`: registra tempo de execucao.

Ambos interceptam `controller`, `facade` e `service`.

## Como Rodar

```powershell
.\mvnw.cmd spring-boot:run
```

Se o wrapper falhar no Windows, use um Maven instalado/local:

```powershell
mvn spring-boot:run
```

Variaveis uteis:

```powershell
$env:SERVER_PORT="8083"
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/db_combate"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="postgres"
```

## Como Testar

```powershell
.\mvnw.cmd test
```

Os testes usam H2 em memoria com profile `test` e mockam o `CharacterClient`, entao nao precisam subir o `character-service`.

## Observacoes

- O `character-service` deve estar disponivel em runtime para criar batalhas reais.
- O RabbitMQ e desejavel para consumo de eventos, mas a indisponibilidade do broker nao impede finalizar uma batalha.

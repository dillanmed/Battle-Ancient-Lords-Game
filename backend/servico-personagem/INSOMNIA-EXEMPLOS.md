# Exemplos para Insomnia/Postman

Base URL:

```text
http://localhost:8081
```

## Listar habilidades

```http
GET http://localhost:8081/habilidades
```

## Listar habilidades por classe

Aceita portugues e aliases em ingles.

```http
GET http://localhost:8081/habilidades/classe/ARCHER
```

```http
GET http://localhost:8081/habilidades/classe/ARQUEIRO
```

## Criar personagem

```http
POST http://localhost:8081/personagens
Content-Type: application/json
```

```json
{
  "usuarioId": 1,
  "nome": "Ayla",
  "classe": "ARCHER"
}
```

## Buscar personagem

```http
GET http://localhost:8081/personagens/1
```

## Dados de combate

```http
GET http://localhost:8081/personagens/1/dados-combate
```

## Habilidades disponiveis do personagem

```http
GET http://localhost:8081/personagens/1/habilidades
```

## Adicionar experiencia

```http
PUT http://localhost:8081/personagens/1/experiencia
Content-Type: application/json
```

```json
{
  "experiencia": 120
}
```

## Evoluir personagem

```http
PUT http://localhost:8081/personagens/1/evoluir
```

## Remover personagem

```http
DELETE http://localhost:8081/personagens/1
```

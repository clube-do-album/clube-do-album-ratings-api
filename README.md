# Clube do Album Ratings API

API responsavel pelas futuras avaliacoes de albuns na plataforma Clube do Album.

## Responsabilidade futura

- Avaliacoes de albuns.
- Reviews e notas.
- Publicacao de eventos de avaliacao.

## Tecnologias usadas

- Java 17
- Spring Boot
- Maven

## Como rodar localmente

```bash
mvn spring-boot:run
```

Endpoint inicial:

```http
GET /health
```

Status atual: projeto inicial criado apenas com estrutura base. As funcionalidades serão implementadas nas próximas etapas.

## Docker

Crie um arquivo local de ambiente a partir do exemplo:

```bash
cp .env.example .env
```

Build da imagem:

```bash
docker build -t clube-do-album-ratings-api .
```

Execucao local:

```bash
docker run --env-file .env -p 8082:8082 clube-do-album-ratings-api
```

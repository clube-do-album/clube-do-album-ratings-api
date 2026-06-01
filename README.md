# Clube do Album Ratings API

API responsavel pelas avaliacoes de albuns na plataforma Clube do Album.

## Responsabilidade

- Criar avaliacoes de albuns.
- Atualizar a avaliacao de um usuario para um album.
- Listar avaliacoes por album.
- Listar avaliacoes por usuario.
- Publicar o evento `ALBUM_RATED` no RabbitMQ.

Ainda nao implementa reviews completas, autenticacao real ou calculo de ranking.

## Tecnologias usadas

- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- RabbitMQ
- Maven

## Variaveis de ambiente

Crie um arquivo local a partir do exemplo:

```bash
cp .env.example .env
```

Variaveis esperadas:

```env
SERVER_PORT=8082

DATABASE_URL=jdbc:postgresql://127.0.0.1:15432/clube_do_album_ratings
DATABASE_USERNAME=clube
DATABASE_PASSWORD=clube

RABBITMQ_URL=amqp://clube:clube@localhost:5672
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=clube
RABBITMQ_PASSWORD=clube
RABBITMQ_EXCHANGE=clube-do-album.events
ALBUM_RATED_ROUTING_KEY=album.rated
```

Quando rodar em container na network Docker, use:

```env
DATABASE_URL=jdbc:postgresql://clube-do-album-postgres:5432/clube_do_album_ratings
RABBITMQ_HOST=clube-do-album-rabbitmq
```

## Banco de dados

Crie o database no PostgreSQL da infra local:

```bash
docker exec -it clube-do-album-postgres psql -U clube -d postgres
```

```sql
create database clube_do_album_ratings;
\q
```

Nesta etapa inicial a API usa `spring.jpa.hibernate.ddl-auto=update` para criar a tabela `ratings`.

## Como rodar localmente

```bash
mvn spring-boot:run
```

Como este projeto nao possui Maven Wrapper, instale Maven localmente ou rode via Docker.

## Endpoints

```http
GET /health
```

```http
POST /ratings
```

Body:

```json
{
  "albumId": "uuid-do-album",
  "userId": "uuid-do-usuario",
  "rating": 4.5
}
```

```http
GET /ratings/albums/{albumId}
```

```http
GET /ratings/users/{userId}
```

## Mensageria

RabbitMQ e usado para publicar eventos de avaliacao.

```text
Exchange: clube-do-album.events
Tipo: topic
Routing key: album.rated
Evento publicado: ALBUM_RATED
```

Payload:

```json
{
  "event": "ALBUM_RATED",
  "albumId": "uuid-do-album",
  "userId": "uuid-do-usuario",
  "rating": 4.5,
  "occurredAt": "2026-06-01T18:00:00.000Z"
}
```

## Como testar manualmente

Suba a infraestrutura:

```bash
cd ../clube-do-album-infra
docker compose up -d
```

Rode a API:

```bash
cd ../clube-do-album-ratings-api
mvn spring-boot:run
```

Crie ou atualize uma avaliacao:

```bash
curl -X POST "http://localhost:8082/ratings" \
  -H "Content-Type: application/json" \
  -d '{"albumId":"album-1","userId":"user-1","rating":4.5}'
```

Liste por album:

```bash
curl "http://localhost:8082/ratings/albums/album-1"
```

Liste por usuario:

```bash
curl "http://localhost:8082/ratings/users/user-1"
```

Acesse o RabbitMQ Management:

```text
http://localhost:15672
```

Login:

```text
clube
```

Senha:

```text
clube
```

Verifique se a exchange `clube-do-album.events` existe.

## Docker

Build da imagem:

```bash
docker build -t clube-do-album-ratings-api .
```

Execucao local em container:

```bash
docker run --env-file .env -p 8082:8082 clube-do-album-ratings-api
```

Execucao na network Docker da infra:

```bash
docker run --rm --name clube-do-album-ratings-api \
  --network clube-do-album-network \
  -e DATABASE_URL=jdbc:postgresql://clube-do-album-postgres:5432/clube_do_album_ratings \
  -e DATABASE_USERNAME=clube \
  -e DATABASE_PASSWORD=clube \
  -e RABBITMQ_HOST=clube-do-album-rabbitmq \
  -e RABBITMQ_PORT=5672 \
  -e RABBITMQ_USERNAME=clube \
  -e RABBITMQ_PASSWORD=clube \
  -p 8082:8082 \
  clube-do-album-ratings-api
```

## Status atual

API inicial de avaliacoes implementada com persistencia em PostgreSQL e publicacao do evento `ALBUM_RATED`. Calculo de ranking e consumo desse evento serao implementados em etapa futura.

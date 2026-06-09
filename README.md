# Clube do Album Ratings API

API responsavel pelas avaliacoes de albuns na plataforma Clube do Album.

## Responsabilidade

- Criar avaliacoes de albuns.
- Salvar review textual opcional junto da avaliacao.
- Atualizar a avaliacao de um usuario para um album.
- Listar avaliacoes por album.
- Listar avaliacoes por usuario.
- Listar avaliacoes publicas de outro usuario para perfil publico.
- Publicar o evento `ALBUM_RATED` no RabbitMQ.

Ainda nao implementa autenticacao real interna ou calculo de ranking.

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
  "rating": 4.5,
  "review": "Texto opcional da review, com ate 1000 caracteres."
}
```

O usuario da avaliacao vem do header interno enviado pelo Gateway:

```http
X-User-Id: uuid-do-usuario
```

```http
GET /ratings/albums/{albumId}
```

```http
GET /ratings/users/{userId}
```

Essa rota tambem exige `X-User-Id` e so permite consultar o proprio usuario autenticado.

```http
GET /ratings/users/{userId}/public
```

Essa rota retorna as avaliacoes publicas de um usuario para exibicao no perfil publico.

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

A review fica armazenada na Ratings API e aparece nas rotas de consulta, mas nao e enviada no evento `ALBUM_RATED` nesta etapa.

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
  -H "X-User-Id: user-1" \
  -d '{"albumId":"album-1","rating":4.5,"review":"Uma review curta sobre o album."}'
```

Liste por album:

```bash
curl "http://localhost:8082/ratings/albums/album-1"
```

Liste por usuario:

```bash
curl "http://localhost:8082/ratings/users/user-1"
```

Ao testar pelo Gateway, envie o token JWT no header `Authorization` e nao envie `userId` no body.

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

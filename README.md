# Tic-Tac-Toe backend

Spring Boot 4 + Java 25 REST API for tic-tac-toe: play a friend or an Easy / Medium / Hard AI, with Rematches.

## Run

```sh
docker compose up -d     # PostgreSQL on :5432
./mvnw spring-boot:run   # API on :8080, Flyway migrates on start
./mvnw test              # needs the database running
```

## API

| Method | Path                 | Body                                  |
|--------|----------------------|---------------------------------------|
| `POST` | `/api/game`          | `{playerOne, playerTwo, myFigure}`    |
| `GET`  | `/api/game/{id}`     |                                       |
| `POST` | `/api/game/takeseat` | `{gameId}`                            |
| `POST` | `/api/game/move`     | `{gameId, playerId, index}`           |
| `POST` | `/api/game/rematch`  | `{gameId, playerId}`                  |

Player types: `user`, `easy`, `medium`, `hard`. The `playerId` you get back is your seat; keep it secret ([ADR 0001](docs/adr/0001-player-id-as-capability.md)).

## Layout

```
api/                 controllers, error mapping
core/domain/         Game, Board, Player, AI strategies (no Spring)
core/app/            use cases, GameService, ports
core/infrastructure/ JPA entities, mappers, repository
```

Domain terms are defined in [CONTEXT.md](CONTEXT.md).

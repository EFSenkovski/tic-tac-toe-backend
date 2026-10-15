package com.wsopt.tictactoebackend.api.game;

import com.wsopt.tictactoebackend.core.app.port.in.game.CreateGameUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.GetGameUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.MakeMoveUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.RematchUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.TakePlayerSeatUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.request.GameRequest;
import com.wsopt.tictactoebackend.core.app.port.in.game.request.MakeMoveRequest;
import com.wsopt.tictactoebackend.core.app.port.in.game.request.RematchRequest;
import com.wsopt.tictactoebackend.core.app.port.in.game.request.TakePlayerSeatRequest;
import com.wsopt.tictactoebackend.core.app.port.in.game.response.GameResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final CreateGameUseCase gameCreator;
    private final TakePlayerSeatUseCase takePlayerSeat;
    private final MakeMoveUseCase moveMaker;
    private final GetGameUseCase gameGetter;
    private final RematchUseCase rematcher;

    public GameController(CreateGameUseCase gameCreator, TakePlayerSeatUseCase takePlayerSeat, MakeMoveUseCase moveMaker, GetGameUseCase gameGetter, RematchUseCase rematcher) {
        this.gameCreator = gameCreator;
        this.takePlayerSeat = takePlayerSeat;
        this.moveMaker = moveMaker;
        this.gameGetter = gameGetter;
        this.rematcher = rematcher;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGame(@PathVariable UUID id) {
        var game = gameGetter.getGame(id);

        return ResponseEntity.ok(game);
    }

    @PostMapping
    public ResponseEntity<GameResponse> createNewGame(@RequestBody GameRequest request) {
        var game = gameCreator.createGame(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(game.gameId())
                .toUri();

        return ResponseEntity.created(location).body(game);
    }

    @PostMapping("/takeseat")
    public ResponseEntity<GameResponse> takeASeat(@Valid @RequestBody TakePlayerSeatRequest request) {
        var game = takePlayerSeat.takePlayerSeat(request.gameId());

        return ResponseEntity.ok(game);
    }

    @PostMapping("/move")
    public ResponseEntity<GameResponse> makeMove(@Valid @RequestBody MakeMoveRequest request) {
        var game = moveMaker.makeMove(request);

        return ResponseEntity.ok(game);
    }

    @PostMapping("/rematch")
    public ResponseEntity<GameResponse> rematch(@Valid @RequestBody RematchRequest request) {
        var game = rematcher.rematch(request);

        return ResponseEntity.ok(game);
    }
}

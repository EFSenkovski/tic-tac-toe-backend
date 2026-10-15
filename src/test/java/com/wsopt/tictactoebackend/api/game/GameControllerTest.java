package com.wsopt.tictactoebackend.api.game;

import com.wsopt.tictactoebackend.core.app.exception.ApplicationException;
import com.wsopt.tictactoebackend.core.app.exception.GameNotFoundException;
import com.wsopt.tictactoebackend.core.app.port.in.game.CreateGameUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.GetGameUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.MakeMoveUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.RematchUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.TakePlayerSeatUseCase;
import com.wsopt.tictactoebackend.core.app.port.in.game.request.MakeMoveRequest;
import com.wsopt.tictactoebackend.core.app.port.in.game.request.RematchRequest;
import com.wsopt.tictactoebackend.core.app.port.in.game.response.GameResponse;
import com.wsopt.tictactoebackend.core.app.port.in.shared.PlayerResponse;
import com.wsopt.tictactoebackend.core.domain.exception.DomainException;
import com.wsopt.tictactoebackend.core.domain.game.Figure;
import com.wsopt.tictactoebackend.core.domain.game.GamePhase;
import com.wsopt.tictactoebackend.core.domain.game.GameStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CreateGameUseCase createGameUseCase;
    @MockitoBean
    TakePlayerSeatUseCase takePlayerSeatUseCase;
    @MockitoBean
    MakeMoveUseCase makeMoveUseCase;
    @MockitoBean
    GetGameUseCase getGameUseCase;
    @MockitoBean
    RematchUseCase rematchUseCase;

    @Test
    void given_ExistingGame_WhenGettingById_ThenShouldReturnGameResponse() throws Exception {
        var gameId = UUID.randomUUID();
        when(getGameUseCase.getGame(gameId)).thenReturn(
                new GameResponse(gameId, Figure.X, null, GamePhase.PLAYING, GameStatus.NOT_FINISHED, "X___O____", List.of(), null));

        mockMvc.perform(get("/api/game/{id}", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(gameId.toString()))
                .andExpect(jsonPath("$.currentFigure").value("X"))
                .andExpect(jsonPath("$.phase").value("PLAYING"))
                .andExpect(jsonPath("$.status").value("NOT_FINISHED"))
                .andExpect(jsonPath("$.board").value("X___O____"))
                .andExpect(jsonPath("$.myPlayer").doesNotExist())
                .andExpect(jsonPath("$.rematchAskedBy").isEmpty())
                .andExpect(jsonPath("$.nextGameId").isEmpty());
    }

    @Test
    void given_FinishedGameWithRematch_WhenGettingById_ThenShouldShowWhoAskedAndTheNextGame() throws Exception {
        var gameId = UUID.randomUUID();
        var nextGameId = UUID.randomUUID();
        when(getGameUseCase.getGame(gameId)).thenReturn(
                new GameResponse(gameId, Figure.O, null, GamePhase.FINISHED, GameStatus.XWINS, "XXXOO____",
                        List.of(Figure.X, Figure.O), nextGameId));

        mockMvc.perform(get("/api/game/{id}", gameId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rematchAskedBy[0]").value("X"))
                .andExpect(jsonPath("$.rematchAskedBy[1]").value("O"))
                .andExpect(jsonPath("$.nextGameId").value(nextGameId.toString()))
                .andExpect(jsonPath("$.myPlayer").doesNotExist());
    }

    @Test
    void given_RematchAgreed_WhenAskingForRematch_ThenShouldReturnNextGameWithMySeat() throws Exception {
        var nextGameId = UUID.randomUUID();
        var playerId = UUID.randomUUID();
        when(rematchUseCase.rematch(any(RematchRequest.class))).thenReturn(
                new GameResponse(nextGameId, Figure.X, new PlayerResponse(playerId, "X"), GamePhase.PLAYING,
                        GameStatus.NOT_FINISHED, "_________", List.of(), null));

        mockMvc.perform(post("/api/game/rematch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rematchBody()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value(nextGameId.toString()))
                .andExpect(jsonPath("$.myPlayer.playerId").value(playerId.toString()))
                .andExpect(jsonPath("$.myPlayer.figure").value("X"));
    }

    @Test
    void given_GameNotOver_WhenAskingForRematch_ThenShouldReturn409() throws Exception {
        when(rematchUseCase.rematch(any(RematchRequest.class)))
                .thenThrow(new ApplicationException("Game is not over"));

        mockMvc.perform(post("/api/game/rematch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rematchBody()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Game is not over"));
    }

    @Test
    void given_MissingPlayerId_WhenAskingForRematch_ThenShouldReturn400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/game/rematch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("{\"gameId\": \"%s\"}", UUID.randomUUID())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.playerId").exists());
    }

    @Test
    void given_UnknownGame_WhenGettingById_ThenShouldReturn404() throws Exception {
        var gameId = UUID.randomUUID();
        when(getGameUseCase.getGame(gameId)).thenThrow(new GameNotFoundException(gameId));

        mockMvc.perform(get("/api/game/{id}", gameId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Game " + gameId + " does not exist!"));
    }

    @Test
    void given_ApplicationRuleViolation_WhenMakingMove_ThenShouldReturn409() throws Exception {
        when(makeMoveUseCase.makeMove(any(MakeMoveRequest.class)))
                .thenThrow(new ApplicationException("Game is not in progress"));

        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moveBody(0)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Game is not in progress"));
    }

    @Test
    void given_DomainRuleViolation_WhenMakingMove_ThenShouldReturn422() throws Exception {
        when(makeMoveUseCase.makeMove(any(MakeMoveRequest.class)))
                .thenThrow(new DomainException("Cell is filled up, you need to choose another"));

        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moveBody(0)))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.detail").value("Cell is filled up, you need to choose another"));
    }

    @Test
    void given_IndexOutOfRange_WhenMakingMove_ThenShouldReturn400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/game/move")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(moveBody(9)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.index").exists());
    }

    @Test
    void given_MissingGameId_WhenTakingSeat_ThenShouldReturn400WithFieldError() throws Exception {
        mockMvc.perform(post("/api/game/takeseat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.gameId").exists());
    }

    @Test
    void given_MalformedUuid_WhenGettingById_ThenShouldReturn400() throws Exception {
        mockMvc.perform(get("/api/game/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest());
    }

    private static String rematchBody() {
        return String.format("""
                {"gameId": "%s", "playerId": "%s"}
                """, UUID.randomUUID(), UUID.randomUUID());
    }

    private static String moveBody(int index) {
        return String.format("""
                {"gameId": "%s", "playerId": "%s", "index": %d}
                """, UUID.randomUUID(), UUID.randomUUID(), index);
    }
}

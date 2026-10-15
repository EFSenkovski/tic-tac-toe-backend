package com.wsopt.tictactoebackend.core.domain.game;

import com.wsopt.tictactoebackend.core.domain.exception.DomainException;
import com.wsopt.tictactoebackend.core.domain.game.player.AiPlayer;
import com.wsopt.tictactoebackend.core.domain.game.player.Player;
import com.wsopt.tictactoebackend.core.domain.game.player.PlayerFactory;
import com.wsopt.tictactoebackend.core.domain.game.player.Rematch;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class Game {
    public static final int[][] WINNING_INDEXES = {
            {0, 1, 2},
            {0, 3, 6},
            {0, 4, 8},
            {1, 4, 7},
            {2, 5, 8},
            {2, 4, 6},
            {3, 4, 5},
            {6, 7, 8}
    };

    private final UUID externalId;
    private final Board board;
    private GameStatus gameStatus;
    private GamePhase gamePhase;
    private Player playerOne;
    private Player playerTwo;
    private Player currentPlayer;
    private UUID nextGameId;


    public Game(Player playerOne, Player playerTwo) {
        this.externalId = UUID.randomUUID();
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.board = new Board();
        this.gameStatus = GameStatus.NOT_FINISHED;
        if (playerOne == null || playerTwo == null) {
            this.gamePhase = GamePhase.WAITING_FOR_SECOND_PLAYER;
            this.currentPlayer = playerTwo == null ?
                    playerOne :
                    playerTwo;
        } else {
            this.gamePhase = GamePhase.PLAYING;
            this.currentPlayer = playerOne;
        }
        if (this.currentPlayer != null && !this.currentPlayer.getPlayerType().isHuman()) {
            makeAiMove();
        }
    }

    public Game(UUID externalId, Board board, GameStatus gameStatus, GamePhase gamePhase, Player playerOne, Player playerTwo, Player currentPlayer, UUID nextGameId) {
        this.externalId = externalId;
        this.board = board;
        this.gameStatus = gameStatus;
        this.gamePhase = gamePhase;
        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
        this.currentPlayer = currentPlayer;
        this.nextGameId = nextGameId;
    }

    public UUID getExternalId() {
        return externalId;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public Player getPlayerOne() {
        return playerOne;
    }

    public Player getPlayerTwo() {
        return playerTwo;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public UUID getNextGameId() {
        return nextGameId;
    }

    public List<Figure> getRematchAskedBy() {
        return seats()
                .filter(seat -> seat.getRematch() != Rematch.NOT_ASKED)
                .map(Player::getFigure)
                .toList();
    }

    /**
     * A Player of this finished Game asks for a Rematch. Asking again before being seated changes nothing.
     */
    public void askForRematch(Player player) {
        if (gamePhase != GamePhase.FINISHED) {
            throw new DomainException(String.format("Game %s is not over (phase %s)", this.externalId, gamePhase));
        }
        var seat = seatOf(player);
        if (seat.getRematch() == Rematch.SEATED) {
            throw new DomainException(String.format("Player %s has already taken their seat in a rematch", player.getExternalId()));
        }
        seat.askForRematch();
    }

    /**
     * Every human Player has asked; the machine always agrees.
     */
    public boolean isRematchAgreed() {
        return seats().allMatch(seat -> !seat.getPlayerType().isHuman() || seat.getRematch() != Rematch.NOT_ASKED);
    }

    /**
     * Creates the next Game with the same Player Types and every Figure swapped, and links this Game to it.
     */
    public Game rematch() {
        if (!isRematchAgreed()) {
            throw new DomainException(String.format("Not every player of game %s has asked for a rematch", this.externalId));
        }
        if (nextGameId != null) {
            throw new DomainException(String.format("Game %s already has a rematch", this.externalId));
        }
        var next = new Game(PlayerFactory.buildPlayer(playerTwo.getPlayerType(), Figure.X),
                PlayerFactory.buildPlayer(playerOne.getPlayerType(), Figure.O));
        this.nextGameId = next.getExternalId();
        return next;
    }

    /**
     * Hands a Player who asked for the Rematch their seat in the next Game: the one with the opposite Figure.
     */
    public Player takeRematchSeat(Player player, Game next) {
        if (nextGameId == null || !nextGameId.equals(next.getExternalId())) {
            throw new DomainException(String.format("Game %s is not the rematch of game %s", next.getExternalId(), this.externalId));
        }
        var seat = seatOf(player);
        if (seat.getRematch() != Rematch.ASKED) {
            throw new DomainException(String.format("Player %s has no rematch seat waiting", player.getExternalId()));
        }
        seat.takeRematchSeat();
        return next.seatWith(seat.getFigure().opposite());
    }

    public void makeMove(int index, Player player) {
        try {
            if (gamePhase != GamePhase.PLAYING) {
                throw new DomainException(String.format("Game %s is not in progress (phase %s)", this.externalId, gamePhase));
            }
            if (!isPlayerInGame(player)) {
                throw new DomainException(String.format("Invalid player %s for game %s", player.getExternalId(), this.externalId));
            }
            if (!currentPlayer.getExternalId().equals(player.getExternalId())) {
                throw new DomainException(String.format("Not %s player's turn on game %s", player.getExternalId(), this.externalId));
            }
            if (isInvalidCoordinate(index)) {
                throw new DomainException("Coordinates should be from 0 to 8! " + index);
            }
            this.board.addToBoard(index, currentPlayer.getFigure());
            gameStatus = calculateGameStatus();
            if (gameStatus != GameStatus.NOT_FINISHED) {
                gamePhase = GamePhase.FINISHED;
            }
            flipPlayer();
            if (gamePhase == GamePhase.PLAYING && !currentPlayer.getPlayerType().isHuman()) {
                Thread.sleep(1000);
                makeAiMove();
            }

        } catch (NumberFormatException e) {
            throw new DomainException("You should enter numbers!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public GameStatus calculateGameStatus() {
        return calculateGameStatus(board.snapshot());
    }

    public static GameStatus calculateGameStatus(List<Figure> cells) {
        int hits;
        for (int[] winningIndexes : WINNING_INDEXES) {
            Figure lastFigure = null;
            hits = 1;
            for (int index : winningIndexes) {
                Figure figureOnIndex = cells.get(index);
                if (figureOnIndex == null) {
                    break;
                }
                if (lastFigure == null) {
                    lastFigure = figureOnIndex;
                    continue;
                }
                if (!figureOnIndex.equals(lastFigure)) {
                    break;
                }
                hits++;
            }
            if (hits == 3) {
                return lastFigure == Figure.X ?
                        GameStatus.XWINS
                        : GameStatus.OWINS;
            }
        }

        boolean isBoardFull = cells.stream().allMatch(Objects::nonNull);
        return isBoardFull ?
                GameStatus.DRAW
                : GameStatus.NOT_FINISHED;
    }

    public boolean isFigureFilled(int index) {
        return this.board.isFigureFilled(index);
    }

    public List<Figure> snapshot() {
        return this.board.snapshot();
    }

    public Board getBoard() {
        return board;
    }

    public boolean areTwoPlayerSeatsTaken() {
        return this.playerOne != null &&
                this.playerTwo != null;
    }

    public void setMissingPlayer(Player player) {
        if (this.playerOne == null) {
            this.playerOne = player;
        } else {
            this.playerTwo = player;
        }
        this.gamePhase = GamePhase.PLAYING;
    }

    private boolean isInvalidCoordinate(int n) {
        return ((n < 0) || (n > 8));
    }

    private Stream<Player> seats() {
        return Stream.of(playerOne, playerTwo).filter(Objects::nonNull);
    }

    private Player seatOf(Player player) {
        if (!isPlayerInGame(player)) {
            throw new DomainException(String.format("Invalid player %s for game %s", player.getExternalId(), this.externalId));
        }
        return hasExternalId(playerOne, player) ? playerOne : playerTwo;
    }

    private Player seatWith(Figure figure) {
        return playerOne.getFigure() == figure ? playerOne : playerTwo;
    }

    private boolean isPlayerInGame(Player player) {
        return player != null && (hasExternalId(playerOne, player) || hasExternalId(playerTwo, player));
    }

    private static boolean hasExternalId(Player seat, Player player) {
        return seat != null && seat.getExternalId().equals(player.getExternalId());
    }

    private void flipPlayer() {
        if (currentPlayer == playerOne) {
            currentPlayer = playerTwo;
        } else {
            currentPlayer = playerOne;
        }
    }

    private void makeAiMove() {
        AiPlayer aiPlayer = (AiPlayer) currentPlayer;
        int index = aiPlayer.getNextMoveIndex(this);
        makeMove(index, aiPlayer);
    }
}

package com.cmpt213.as4.controller;

import ca.cmpt213.as3.CellLocation;
import ca.cmpt213.as3.CellState;
import ca.cmpt213.as3.Game;
import com.cmpt213.as4.restapi.ApiBoardWrapper;
import com.cmpt213.as4.restapi.ApiGameWrapper;
import com.cmpt213.as4.restapi.ApiLocationWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Controller class for updating the UI and setting up the functionality of
 * the Fortress Defense game. Main System includes getting the game ID, updating
 * board state, and validating http request.
 *
 * */

@RestController
public class GameController {

    private final AtomicInteger ID = new AtomicInteger();
    private final List<ApiGameWrapper> GAME_OBJECTS = new ArrayList<>();
    private final int WIDTH = 10;
    private final int HEIGHT = 10;

    private boolean isCheatModeOn;

    private ApiLocationWrapper location;
    private Game fortressDefenceGame;


    @GetMapping("/api/about")
    public String getName() {

        return "Alex Biscoveanu";
    }

    @GetMapping("/api/games")
    public List<ApiGameWrapper> getGameObjectsList() {

        return GAME_OBJECTS;
    }

    @PostMapping("/api/games")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiGameWrapper createNewGame() {
        int gameNumber = ID.incrementAndGet();
        isCheatModeOn = false;
        location = null;

        final int MAX_NUMBER_OF_TANKS = 5;
        fortressDefenceGame = new Game(MAX_NUMBER_OF_TANKS);

        final int MAX_FORTRESS_HEALTH = fortressDefenceGame.getFortressHealth();
        final boolean IS_GAME_WON = fortressDefenceGame.hasUserWon();
        final boolean IS_GAME_LOST = fortressDefenceGame.hasUserLost();

        ApiGameWrapper newGame = ApiGameWrapper.createNewGameInstance(gameNumber, MAX_NUMBER_OF_TANKS,
                MAX_FORTRESS_HEALTH, IS_GAME_WON, IS_GAME_LOST);

        GAME_OBJECTS.add(newGame);

        return newGame;
    }

    @GetMapping("/api/games/{id}")
    public ApiGameWrapper getGame(@PathVariable("id") int gameId) {
        for (ApiGameWrapper game : GAME_OBJECTS) {
            if (game.gameNumber == gameId) {
                return game;
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "404");
    }

    @GetMapping("/api/games/{id}/board")
    public ApiBoardWrapper getBoardCurrentState(@PathVariable("id") int gameId) {

        ApiBoardWrapper board = ApiBoardWrapper.createNewGameBoardInstance(WIDTH, HEIGHT);

        for (ApiGameWrapper game : GAME_OBJECTS) {
            if (game.gameNumber == gameId) {

                for (int i = 0; i < board.boardWidth; i++) {
                    for (int j = 0; j < board.boardHeight; j++) {

                        CellLocation cell = new CellLocation(i, j);
                        CellState getState = fortressDefenceGame.getCellState(cell);

                        if (location != null) {
                            cell = new CellLocation(location.row, location.col);
                            fortressDefenceGame.recordPlayerShot(cell);
                        }

                        if (getState.isHidden() && !isCheatModeOn) {
                            board.cellStates[i][j] = "fog";

                        } else if (getState.hasTank()) {

                            if (isCheatModeOn) {
                                if (getState.hasBeenShot()) {
                                    board.cellStates[i][j] = "miss";

                                } else {
                                    board.cellStates[i][j] = "tank";
                                }

                            } else {
                                board.cellStates[i][j] = "miss";
                            }

                        } else if (getState.hasBeenShot()) {
                            board.cellStates[i][j] = "hit";

                        } else {
                            board.cellStates[i][j] = "field";
                        }

                    }

                }

                fortressDefenceGame.fireTanks();
                game.lastTankDamages = fortressDefenceGame.getLatestTankDamages();
                game.fortressHealth = fortressDefenceGame.getFortressHealth();

                if (fortressDefenceGame.hasUserWon()) {
                    game.isGameWon = true;

                } else if (fortressDefenceGame.hasUserLost()) {
                    game.isGameLost = true;
                }

                return board;
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "404");
    }

    @PostMapping("/api/games/{id}/moves")
    public void makeAMove(@PathVariable("id") int gameId, @RequestBody ApiLocationWrapper requestLocation) {

        location = ApiLocationWrapper.createNewLocationInstance(requestLocation.row, requestLocation.col);

        for (ApiGameWrapper game : GAME_OBJECTS) {
            if (game.gameNumber == gameId) {

                final int MIN_BOUNDS = 0;
                boolean isOutWidthBounds = location.row < MIN_BOUNDS || location.row >= WIDTH;
                boolean isOutHeightBounds = location.col < MIN_BOUNDS || location.col >= HEIGHT;

                if (isOutWidthBounds || isOutHeightBounds) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "400");
                }

                throw new ResponseStatusException(HttpStatus.ACCEPTED, "202");
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "404");
    }

    @PostMapping("/api/games/{id}/cheatstate")
    public void activateCheat(@PathVariable("id") int gameId, @RequestBody String showAllCells) {
        for (ApiGameWrapper game : GAME_OBJECTS) {
            if (game.gameNumber == gameId) {

                if (!showAllCells.equals("SHOW_ALL")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "400");
                }

                isCheatModeOn = true;
                throw new ResponseStatusException(HttpStatus.ACCEPTED, "202");
            }
        }

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "404");
    }
}

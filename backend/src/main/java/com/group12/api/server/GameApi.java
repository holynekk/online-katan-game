package com.group12.api.server;

import com.group12.api.request.game.GameCreateRequest;
import com.group12.api.request.game.GameHistoryCreateRequest;
import com.group12.api.response.GameHistoryResponse;
import com.group12.api.response.GameResponse;
import com.group12.entity.Game;
import com.group12.entity.GameHistory;
import com.group12.entity.User;
import com.group12.repository.GameHistoryRepository;
import com.group12.repository.GameRepository;
import com.group12.repository.UserRepository;
import com.group12.util.EncryptDecryptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.group12.api.server.UserApi.SECRET_KEY;

/**
 * The {@code GameApi} class handles HTTP requests related to game management in an online gaming
 * platform. It provides endpoints for functionalities such as fetching game data, creating new
 * games, listing active games, closing a game, and recording game history for users.
 */
@RestController
@RequestMapping("/api/game")
public class GameApi {

  @Autowired GameRepository repository;

  @Autowired UserRepository userRepository;

  @Autowired GameHistoryRepository gameHistoryRepository;

  /**
   * A get request to fetch a game data for a specific game with its id.
   *
   * @param providedGameId - provided game id with request parameters.
   * @return - GameResponse instance.
   */
  @GetMapping(value = "", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<GameResponse> getGameById(
      @RequestParam(name = "gameId") int providedGameId) {
    Optional<Game> optionalGame = repository.findGameById(providedGameId);
    if (optionalGame.isPresent()) {
      Game game = optionalGame.get();
      GameResponse response =
          new GameResponse(
              game.getGameId(),
              game.getGameName(),
              game.getGameDescription(),
              game.getPasswordRequired(),
              game.getGameLeader().getDisplayName(),
              game.getOnline(),
              game.getStarted(),
              game.getFinished());
      return ResponseEntity.status(HttpStatus.OK)
          .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
          .body(response);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
  }

  /**
   * A post request to create a game.
   *
   * @param request - http request sent by client
   * @return - GameResponse instance.
   */
  @PostMapping(
      value = "",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<GameResponse> createGame(@RequestBody GameCreateRequest request) {
    Optional<User> user = userRepository.findById(request.getGameLeader());
    if (user.isPresent()) {
      Game savedGame =
          repository.saveAndFlush(
              new Game(
                  request.getGameName(),
                  request.getGameDescription(),
                  request.getGamePassword(),
                  request.getPasswordRequired(),
                  user.get(),
                  request.getOnline(),
                  false,
                  false));

      GameResponse response =
          new GameResponse(
              savedGame.getGameId(),
              savedGame.getGameName(),
              savedGame.getGameDescription(),
              savedGame.getPasswordRequired(),
              savedGame.getGameLeader().getDisplayName(),
              savedGame.getOnline(),
              savedGame.getStarted(),
              savedGame.getFinished());
      return ResponseEntity.status(HttpStatus.OK)
          .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
          .body(response);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
  }

  /**
   * An endpoint to return all active (currently online games).
   *
   * @return - List of GameResponse which contains active games.
   */
  @GetMapping(value = "/list", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<List<GameResponse>> getActiveGamesList() {
    Optional<List<Game>> optionalActiveGames = repository.findAllActiveGames();
    if (optionalActiveGames.isPresent()) {
      List<GameResponse> gameList = new ArrayList<>();
      for (Game gm : optionalActiveGames.get()) {
        gameList.add(
            new GameResponse(
                gm.getGameId(),
                gm.getGameName(),
                gm.getGameDescription(),
                gm.getPasswordRequired(),
                gm.getGameLeader().getDisplayName(),
                gm.getOnline(),
                gm.getStarted(),
                gm.getFinished()));
      }
      return ResponseEntity.status(HttpStatus.OK)
          .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
          .body(gameList);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
  }

  /**
   * An endpoint to change game's online status from still playing to finished.
   *
   * @param gameId - Provided game's id which will be closed.
   * @return - A string message about the result.
   */
  @PutMapping(
      value = "/closeGame",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> closeGame(@RequestParam(required = true) Integer gameId) {
    Optional<Game> gameOptional = repository.findGameById(gameId);
    if (gameOptional.isPresent()) {
      Game game = gameOptional.get();
      game.setIsOnline(false);
      repository.save(game);
      return ResponseEntity.status(HttpStatus.OK).body("The game has been closed!");
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
  }

  /**
   * An endpoint to log game result for a specific user and a game.
   *
   * @param request - http request sent by client
   * @return - GameHistory instance that has been created.
   */
  @PostMapping(value = "/game-history", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GameHistory> createGameHistoryByUsername(
      @RequestBody(required = true) GameHistoryCreateRequest request)
      throws InvalidAlgorithmParameterException,
          IllegalBlockSizeException,
          BadPaddingException,
          InvalidKeyException {
    String encryptedUsername =
        EncryptDecryptUtil.encryptAes(request.getUsername().toLowerCase(), SECRET_KEY);
    Optional<User> optionalUser = userRepository.findByUsername(encryptedUsername);
    Optional<Game> optionalGame = repository.findGameById(request.getGameId());
    if (optionalUser.isPresent() && optionalGame.isPresent()) {
      GameHistory newGameHistory =
          gameHistoryRepository.saveAndFlush(
              new GameHistory(
                  request.getTotalScore(),
                  request.getDidWon(),
                  request.getTime(),
                  optionalUser.get(),
                  optionalGame.get()));

      return ResponseEntity.status(HttpStatus.OK).body(newGameHistory);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
  }
}

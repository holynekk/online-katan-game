package com.group12.api.server;

import com.group12.api.request.game.GameCreateRequest;
import com.group12.api.response.GameResponse;
import com.group12.entity.Game;
import com.group12.entity.User;
import com.group12.repository.GameRepository;
import com.group12.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/game")
public class GameApi {

  @Autowired GameRepository repository;

  @Autowired UserRepository userRepository;

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

  @PostMapping(
      value = "",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<GameResponse> getGameById(@RequestBody GameCreateRequest request) {
    Optional<User> user = userRepository.findById(request.getGameLeader());
    if (user.isPresent()) {
      Game newGame =
          new Game(
              request.getGameName(),
              request.getGameDescription(),
              request.getGamePassword(),
              request.getPasswordRequired(),
              user.get(),
              request.getOnline(),
              false,
              false);
      repository.save(newGame);
      return ResponseEntity.status(HttpStatus.OK)
          .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
          .body(
              new GameResponse(
                  newGame.getGameId(),
                  newGame.getGameName(),
                  newGame.getGameDescription(),
                  newGame.getPasswordRequired(),
                  newGame.getGameLeader().getDisplayName(),
                  newGame.getOnline(),
                  newGame.getStarted(),
                  newGame.getFinished()));
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
  }

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
}

package com.group12.repository;

import com.group12.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {

  @Query(value = "SELECT g FROM Game g WHERE g.gameId = ?1")
  public Optional<Game> findGameById(int gameId);

  @Query(value = "SELECT g FROM Game g WHERE g.isOnline = true")
  public Optional<List<Game>> findAllActiveGames();
}

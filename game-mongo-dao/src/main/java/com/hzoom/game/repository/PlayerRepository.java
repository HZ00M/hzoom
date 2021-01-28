package com.hzoom.game.repository;

import com.hzoom.game.entity.Player;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerRepository extends MongoRepository<Player,Long> {
//    Page<Player> queryAllByPlayerId(Long playerId);
}

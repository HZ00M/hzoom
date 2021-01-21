package com.hzoom.game.repository;

import com.hzoom.game.entity.Arena;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ArenaRepository extends MongoRepository<Arena,Long> {
}

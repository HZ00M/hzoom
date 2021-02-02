package com.hzoom.game.repository;

import com.hzoom.game.entity.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface UserAccountRepository extends MongoRepository<UserAccount,String> {

//    Page<UserAccount>  findUserAccountByUserIdEquals(Long userId);

    @Query(value = "{'creatTime':{'$lt':?0}}")
    List<UserAccount> findTest(Date now);
}

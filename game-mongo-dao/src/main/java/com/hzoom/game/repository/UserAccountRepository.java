package com.hzoom.game.repository;

import com.hzoom.game.entity.UserAccount;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserAccountRepository extends MongoRepository<UserAccount,String> {

//    Page<UserAccount>  findUserAccountByUserIdEquals(Long userId);
//
//    @Query(value = "{'creatTime':{'$gt':?1}}")
//    Page<UserAccount> findTest(Date now);
}

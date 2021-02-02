package com.hzoom.game;
import com.hzoom.game.dao.UserAccountDao;
import com.hzoom.game.entity.UserAccount;
import com.hzoom.game.repository.UserAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = {BirdApplication.class})
@TestExecutionListeners(listeners = MockitoTestExecutionListener.class)
@Slf4j
public class UserAccountRepositoryTest extends AbstractTestNGSpringContextTests {
    @SpyBean
    private UserAccountDao userAccountDao;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Test
    public void findById(){
        Optional<UserAccount> userAccount = userAccountDao.findById("123");
        log.info("{}",userAccount.get());
    }

    @Test
    public void test(){
        List<UserAccount> all = userAccountRepository.findAll();
        log.info("{}",all);
    }

    @Test
    public void test1(){
        List<UserAccount> test = userAccountRepository.findTest(new Date());
        log.info("{}",test);
    }
}

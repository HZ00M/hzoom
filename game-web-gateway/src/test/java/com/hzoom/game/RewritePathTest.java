package com.hzoom.game;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Slf4j
public class RewritePathTest extends BaseTest {

    @BeforeClass
    public void BeforeClass() {
        log.info("start get GameCenter Test");
    }

    @Test(priority = 1)
    @Description("Get game-center")
    public void getGameCenter() throws Exception {
        log.info("result :{}", "test");
    }
}

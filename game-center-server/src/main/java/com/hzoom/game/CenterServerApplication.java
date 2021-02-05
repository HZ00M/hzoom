package com.hzoom.game;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@MapperScan("com.hzoom.game.common.mapper")
public class CenterServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CenterServerApplication.class,args);
    }
}

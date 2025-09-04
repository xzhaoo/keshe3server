package com.keshe3.keshe3server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = { "com.keshe3" })
@MapperScan(basePackages = { "com.keshe3.**.mapper" })
public class Keshe3serverApplication {

    public static void main(String[] args) {

        SpringApplication.run(Keshe3serverApplication.class, args);
        System.out.println("启动成功");
    }

}

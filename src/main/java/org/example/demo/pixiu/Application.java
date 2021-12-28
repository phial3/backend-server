package org.example.demo.pixiu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @description:
 * @project: backend-server
 * @datetime: 2021/12/27 16:30 Monday
 */
@MapperScan("org.example.demo.pixiu.mapper")
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

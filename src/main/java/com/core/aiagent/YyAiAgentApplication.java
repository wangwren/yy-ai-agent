package com.core.aiagent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.core.aiagent.mapper")
public class YyAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(YyAiAgentApplication.class, args);
    }

}

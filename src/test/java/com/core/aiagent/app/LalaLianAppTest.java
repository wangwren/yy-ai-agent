package com.core.aiagent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LalaLianAppTest {

    @Resource
    private LalaLianApp lalaLianApp;

    @Test
    void doChat4Rag() {

        String chatId = UUID.randomUUID().toString();
        System.out.println("chatId: " + chatId);
        String message = "你是大屁股圆圆";
        String answer = lalaLianApp.doChat4Rag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChat4RagCloud() {
        String chatId = UUID.randomUUID().toString();
        System.out.println("chatId: " + chatId);
        String message = "你是大屁股圆圆";
        String answer = lalaLianApp.doChat4RagCloud(message, chatId);
        Assertions.assertNotNull(answer);
    }
}
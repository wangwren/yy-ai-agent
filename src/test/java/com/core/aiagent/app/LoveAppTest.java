package com.core.aiagent.app;

import cn.hutool.core.lang.UUID;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        System.out.println("chatId: " + chatId);
        // 第一轮对话
        String message = "我是一个程序员，我叫人人";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        // 第二轮对话
        message = "我的另一半是大屁股圆圆，我想让她更爱我";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        // 第三轮对话
        message = "我的另一半是谁来着，我刚刚提到过";
        answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChat4Report() {
        String chatId = UUID.randomUUID().toString();
        System.out.println("chatId: " + chatId);
        // 第一轮对话
        String message = "我是一个程序员，我叫人人，我的女朋友叫大屁股圆圆，如何让她更爱我";
        LoveApp.LoveReport loveReport = loveApp.doChat4Report(message, chatId);
        Assertions.assertNotNull(loveReport);
    }
}
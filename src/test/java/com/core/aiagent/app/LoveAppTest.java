package com.core.aiagent.app;

import cn.hutool.core.lang.UUID;
import com.core.aiagent.rag.LoveAppDocumentLoader;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class LoveAppTest {

    @Resource
    private LoveApp loveApp;

    @Resource
    private PgVectorStore pgVectorStore;

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;


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

    @Test
    void testChatMemory() {
        String chatId = "804e52bf-aa75-4a07-bb2e-ec93f47f4e1e";
        System.out.println("chatId: " + chatId);
        // 第一轮对话
        String message = "我是谁，我的另一半叫什么";
        String answer = loveApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChat4Rag() {
        String chatId = UUID.randomUUID().toString();
        System.out.println("chatId: " + chatId);
        // 第一轮对话，已经在md文档中加上了这个问题相关的信息
        String message = "我的女朋友叫什么，我们准备什么时候结婚";
        String answer = loveApp.doChat4Rag(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChat4RagCloud() {
        String chatId = UUID.randomUUID().toString();
        System.out.println("chatId: " + chatId);
        // 第一轮对话
        String message = "我是一个程序员，我叫人人";
        String answer = loveApp.doChat4RagCloud(message, chatId);
        Assertions.assertNotNull(answer);

        // 第二轮对话
        message = "我的女朋友叫什么，我又是谁";
        answer = loveApp.doChat4RagCloud(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChat4RagPgVector() {
        // 添加文档至PgVectorStore向量数据库
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        pgVectorStore.doAdd(documents);


        String chatId = UUID.randomUUID().toString();
        System.out.println("chatId: " + chatId);
        // 第一轮对话
        String message = "我是一个程序员，我叫人人";
        String answer = loveApp.doChat4RagPgVector(message, chatId);
        Assertions.assertNotNull(answer);

        // 第二轮对话
        message = "我的女朋友叫什么，我又是谁";
        answer = loveApp.doChat4RagPgVector(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChat4Tools() {

        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");

        // 测试文件操作：保存用户档案
        testMessage("保存我的恋爱档案为文件");
    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChat4Tools(message, chatId);
        Assertions.assertNotNull(answer);
    }
}
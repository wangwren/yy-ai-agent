package com.core.aiagent.config;

import cn.hutool.core.lang.UUID;
import com.core.aiagent.app.LoveApp;
import com.core.aiagent.rag.LoveAppDocumentLoader;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 向量数据库pgVector
 */
@SpringBootTest
class PgVectorStoreConfigTest {

    @Resource
    private PgVectorStore pgVectorStore;
    @Resource
    private LoveApp loveApp;
    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    /**
     * 测试PgVectorStore
     */
    @Test
    void test() {
        List<Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));
        // 添加文档
        pgVectorStore.add(documents);
        // 相似度查询
        List<Document> results = pgVectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());
        Assertions.assertNotNull(results);
    }

    @Test
    void doChat4Rag() {
        // 添加文档至PgVectorStore向量数据库
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        pgVectorStore.add(documents);

        String chatId = UUID.randomUUID().toString();
        System.out.println("chatId: " + chatId);
        // 第一轮对话，已经在md文档中加上了这个问题相关的信息
        String message = "我的女朋友叫什么，我们准备什么时候结婚";
        String answer = loveApp.doChat4RagPgVector(message, chatId);
        Assertions.assertNotNull(answer);
    }
}
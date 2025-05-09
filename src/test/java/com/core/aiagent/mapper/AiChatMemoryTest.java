package com.core.aiagent.mapper;

import com.core.aiagent.model.AiChatMemory;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan("com.core.aiagent.mapper")
public class AiChatMemoryTest {

    @Autowired
    private AiChatMemoryMapper mapper;

    @Test
    void testMysql() {
        AiChatMemory aiChatMemory = new AiChatMemory();
        aiChatMemory.setType("USER");
        aiChatMemory.setContent("你好");
        aiChatMemory.setConversationId("123456");

        mapper.insert(aiChatMemory);
    }
}

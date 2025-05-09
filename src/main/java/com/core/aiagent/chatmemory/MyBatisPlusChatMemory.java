package com.core.aiagent.chatmemory;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.core.aiagent.mapper.AiChatMemoryMapper;
import com.core.aiagent.model.AiChatMemory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class MyBatisPlusChatMemory implements ChatMemory {

    @Autowired
    private AiChatMemoryMapper mapper;

    @Override
    public void add(String conversationId, Message message) {
        AiChatMemory aiChatMemory = new AiChatMemory();
        aiChatMemory.setConversationId(conversationId);
        aiChatMemory.setType(message.getMessageType().getValue());
        aiChatMemory.setContent(message.getText());

        mapper.insert(aiChatMemory);
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        List<AiChatMemory> aiChatMemories = new ArrayList<>();
        for (Message message : messages) {
            AiChatMemory aiChatMemory = new AiChatMemory();
            aiChatMemory.setConversationId(conversationId);
            aiChatMemory.setType(message.getMessageType().getValue());
            aiChatMemory.setContent(message.getText());
            aiChatMemories.add(aiChatMemory);
        }

        mapper.insert(aiChatMemories);
    }

    @Override
    public List<Message> get(String conversationId, int lastN) {
        // 分页查询最近N条记录
        Page<AiChatMemory> page = new Page<>(1, lastN);
        QueryWrapper<AiChatMemory> wrapper = new QueryWrapper<>();
        wrapper.eq("conversation_id", conversationId)
                .orderByDesc("create_time");

        List<AiChatMemory> aiChatMemories = mapper.selectList(wrapper);
        // 反转列表,使得最新的消息在最后
        Collections.reverse(aiChatMemories);

        // 转换为Message对象
        List<Message> messages = new ArrayList<>();
        for (AiChatMemory aiChatMemory : aiChatMemories) {
            String type = aiChatMemory.getType();
            switch (type) {
                case "user" -> messages.add(new UserMessage(aiChatMemory.getContent()));
                case "assistant" -> messages.add(new AssistantMessage(aiChatMemory.getContent()));
                case "system" -> messages.add(new SystemMessage(aiChatMemory.getContent()));
                default -> throw new IllegalArgumentException("Unknown message type: " + type);
            }
        }
        return messages;
    }

    @Override
    public void clear(String conversationId) {
        // 删除指定会话的所有消息
        QueryWrapper<AiChatMemory> wrapper = new QueryWrapper<>();
        wrapper.eq("conversation_id", conversationId);
        mapper.delete(wrapper);
    }
}

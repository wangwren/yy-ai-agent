package com.core.aiagent.app;

import com.core.aiagent.advisor.MyLoggerAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {

    private ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatModel dashScopeChatModel) {

        // 对话记忆，创建一个内存对话记忆
        ChatMemory chatMemory = new InMemoryChatMemory();

        this.chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                // 指定默认advisor(类似拦截器)，MessageChatMemoryAdvisor实现对话记忆功能,chatMemory是用来保存对话的
                // .defaultAdvisors(...)：注册「要用记忆」的能力。
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new MyLoggerAdvisor()
                )
                .build();
    }

    /**
     *
     * @param message 用户消息
     * @param chatId 对话记忆的id
     * @return ai回复
     */
    public String doChat(String message, String chatId) {

        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                // 指定对话记忆的id和对话记忆的长度(10条)
                // .advisors(spec->...)：告诉该能力「这次用哪个会话」「取多少历史」
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5))
                .call()
                .chatResponse();

        String text = chatResponse.getResult().getOutput().getText();
        //log.info("用户消息: {}, 返回消息: {}", message, text);

        return text;
    }
}

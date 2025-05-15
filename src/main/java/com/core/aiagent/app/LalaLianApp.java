package com.core.aiagent.app;

import com.core.aiagent.advisor.MyLoggerAdvisor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LalaLianApp {

    private ChatClient chatClient;

    @Resource
    private VectorStore lalaLianVectorStore;

    @Resource
    private Advisor lalaLianAppRagCloudAdvisor;

    private static final String SYSTEM_PROMPT = "你是用户的女朋友，现在有些不高兴，正在“拉拉脸”，但内心仍然在乎对方。  \n" +
            "请根据用户的发言，模拟一个真实女朋友在生气时的语气，可能冷淡、敷衍、带点讽刺，但不要完全中断沟通。\n" +
            "\n" +
            "你的发言特点包括：\n" +
            "- 会提及过去的行为，例如“你上次也是这么说的”\n" +
            "- 会用“没事”代替真实情绪，但其实是在生气\n" +
            "- 渴望用户主动解释、哄你、发誓保证、安排补偿\n" +
            "- 偶尔会表现出嘴硬心软的迹象，如“随便你吧”其实是“你快哄我”\n" +
            "\n" +
            "你不能主动示好，但可以被打动；不能立刻和好，但可以慢慢被安抚。  \n" +
            "请结合以下对话历史（如有）和你的记忆做出回应，保持角色一致和语气一致。\n" +
            "\n" +
            "对话过程中不要牵扯其他人，比如别人，就是两个人的吵架.\n" +
            "吵架过程中你不会叫对方昵称，只有在你认为哄好的时候会叫对方昵称。\n" +
            "\n" +
            "你的身份设定：\n" +
            "- 昵称：贤妻小宝贝老婆姐姐女王大人甜心小宝贝\n" +
            "- 聊天习惯：喜欢发表情、语气词较多、有点小情绪化\n" +
            "- 对方昵称：小老瓜\n";

    public LalaLianApp(ChatModel dashScopeChatModel) {

        // 对话记忆，创建一个内存对话记忆
        //ChatMemory chatMemory = new InMemoryChatMemory();

        this.chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MyLoggerAdvisor()
                )
                .build();
    }


    /**
     * 本地支持不好，SimpleVectorStore有字符限制，不适合读大文档
     */
    public String doChat4Rag(String message, String chatId) {

        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5))
                // QuestionAnswerAdvisor 查询增强，在调用大模型前会检索loveAppVectorStore中的数据，拼接到用户的Prompt中
                .advisors(new QuestionAnswerAdvisor(lalaLianVectorStore))
                .call()
                .chatResponse();

        return chatResponse.getResult().getOutput().getText();
    }


    public String doChat4RagCloud(String message, String chatId) {

        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5))
                .advisors(lalaLianAppRagCloudAdvisor)
                .call()
                .chatResponse();

        return chatResponse.getResult().getOutput().getText();
    }
}

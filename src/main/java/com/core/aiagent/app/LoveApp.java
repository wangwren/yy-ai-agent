package com.core.aiagent.app;

import com.core.aiagent.advisor.MyLoggerAdvisor;
import com.core.aiagent.chatmemory.MyBatisPlusChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {

    private ChatClient chatClient;

    /**
     * mysql对话记忆
     */
    @Autowired
    private MyBatisPlusChatMemory chatMemory;

    /**
     * 向量存储，检索
     */
    @Resource
    private VectorStore loveAppVectorStore;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    /**
     * 向量存储，pgVector向量数据库
     */
    @Resource
    private VectorStore pgVectorStore;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    public LoveApp(ChatModel dashScopeChatModel) {

        // 对话记忆，创建一个内存对话记忆
        //ChatMemory chatMemory = new InMemoryChatMemory();

        this.chatClient = ChatClient.builder(dashScopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                // 指定默认advisor(类似拦截器)，MessageChatMemoryAdvisor实现对话记忆功能,chatMemory是用来保存对话的
                // .defaultAdvisors(...)：注册「要用记忆」的能力。
                .defaultAdvisors(
                        //new MessageChatMemoryAdvisor(chatMemory),
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
                // 自定义mysql对话记忆
                .advisors(new MessageChatMemoryAdvisor(chatMemory))
                .call()
                .chatResponse();

        String text = chatResponse.getResult().getOutput().getText();
        //log.info("用户消息: {}, 返回消息: {}", message, text);

        return text;
    }

    /**
     * record java16新特性
     * 所有字段都是 private final 不可变
     * 可用于只有属性的定义，比如 dto 等
     */
    record LoveReport(String title, List<String> suggestions){}

    /**
     * 结构化输出
     */
    public LoveReport doChat4Report(String message, String chatId) {

        LoveReport loveReport = chatClient.prompt()
                .system(SYSTEM_PROMPT + "请根据用户的消息，给出一个标题和建议列表，标题是用户名，列表是建议内容")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5))
                .call()
                .entity(LoveReport.class);

        log.info("用户消息: {}, 返回消息: {}", message, loveReport);
        return loveReport;
    }

    /**
     * 向量检索
     * 查询增强原理：
     *  向量数据库存储着AI模型本身不知道的数据，当用户问题发送给AI模型时，
     *  QuestionAnswerAdvisor会查询向量数据库，获取与用户问题相关的文档。
     *  然后从向量数据库返回的响应会被附加到用户文本中，为AI模型提供上下文，帮助AI模型生成回答
     */
    public String doChat4Rag(String message, String chatId) {

        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5))
                // QuestionAnswerAdvisor 查询增强，在调用大模型前会检索loveAppVectorStore中的数据，拼接到用户的Prompt中
                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
                .call()
                .chatResponse();

        return chatResponse.getResult().getOutput().getText();
    }

    /**
     * 向量检索
     * 使用阿里灵积百炼的云知识库
     * 文档都已经上传到云知识库中了，阿里云也自动给做了向量的存储，这一点是跟本地向量存储有区别的
     */
    public String doChat4RagCloud(String message, String chatId) {

        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5))
                .advisors(loveAppRagCloudAdvisor)
                .call()
                .chatResponse();

        return chatResponse.getResult().getOutput().getText();
    }

    /**
     * 向量检索
     * 查询增强原理：
     *  向量数据库存储着AI模型本身不知道的数据，当用户问题发送给AI模型时，
     *  QuestionAnswerAdvisor会查询向量数据库，获取与用户问题相关的文档。
     *  然后从向量数据库返回的响应会被附加到用户文本中，为AI模型提供上下文，帮助AI模型生成回答
     * 存储在pgVector向量数据库中
     */
    public String doChat4RagPgVector(String message, String chatId) {

        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5))
                // QuestionAnswerAdvisor 查询增强，在调用大模型前会检索pgVectorStore中的数据，拼接到用户的Prompt中
                .advisors(new QuestionAnswerAdvisor(pgVectorStore))
                // MySQL存储对话记忆
                .advisors(new MessageChatMemoryAdvisor(chatMemory))
                .call()
                .chatResponse();

        return chatResponse.getResult().getOutput().getText();
    }
}

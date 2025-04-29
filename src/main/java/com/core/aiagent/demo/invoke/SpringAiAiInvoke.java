package com.core.aiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Spring AI 调用大模型
 * CommandLineRunner 在项目启动时会执行
 */
@Component
public class SpringAiAiInvoke implements CommandLineRunner {

    /**
     * 根据名称注入
     * 注意dashScopeChatModel写法，使用别的模型就要写别的名
     * 在配置文件中spring.ai.dashscope 这个名
     * 否则注入不了
     */
    @Resource
    private ChatModel dashScopeChatModel;

    @Override
    public void run(String... args) throws Exception {

        ChatResponse chatResponse = dashScopeChatModel.call(new Prompt("你是谁"));
        AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
        System.out.println(assistantMessage.getText());
    }
}

package com.core.aiagent.tool;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 一次性给AI提供所有的工具
 */
@Configuration
public class ToolRegistration {

    @Value("${search-api.key}")
    private String searchApiKey;

    @Bean
    public ToolCallback[] allTools() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        WebSearchTool webSearchTool = new WebSearchTool(searchApiKey);
        return ToolCallbacks.from(
            fileOperationTool,
            webSearchTool
        );
    }
}

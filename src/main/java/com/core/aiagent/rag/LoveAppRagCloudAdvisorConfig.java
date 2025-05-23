package com.core.aiagent.rag;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetriever;
import com.alibaba.cloud.ai.dashscope.rag.DashScopeDocumentRetrieverOptions;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoveAppRagCloudAdvisorConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String dashscopeApiKey;

    /**
     * 初始化基于云知识库的检索增强bean
     */
    @Bean
    public Advisor loveAppRagCloudAdvisor() {
        DashScopeApi dashScopeApi = new DashScopeApi(dashscopeApiKey);
        // Spring AI alibaba 利用了Spring AI提供的文档检索特性DocumentRetriever，DashScopeDocumentRetriever实现了该接口，
        // 自定义了一套文档检索的方法，使得程序会调用阿里灵积大模型API来从云知识库中检索文档，而不是从内存中检索文档
        DashScopeDocumentRetriever dashScopeDocumentRetriever = new DashScopeDocumentRetriever(dashScopeApi
                , DashScopeDocumentRetrieverOptions.builder()
                // 指定知识库名称，会从云知识库中检索文档
                .withIndexName("恋爱大师")
                .build());

        // 返回基于云知识库的检索增强RetrievalAugmentationAdvisor
        return RetrievalAugmentationAdvisor.builder()
                // 指定文档检索器
                .documentRetriever(dashScopeDocumentRetriever)
                .build();
    }
}

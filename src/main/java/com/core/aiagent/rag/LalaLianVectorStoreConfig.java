package com.core.aiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class LalaLianVectorStoreConfig {

    @Resource
    private LalaLianAppDocumentLoader lalaLianDocumentLoader;

    @Bean
    public VectorStore lalaLianVectorStore(EmbeddingModel dashscoreEmbeddingModel) {
        // 需要传入一个大模型的EmbeddingModel，将文档转换为向量，实际保存的是向量类型的数据
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(dashscoreEmbeddingModel).build();

        // 加载数据
        List<Document> documents = lalaLianDocumentLoader.loadMarkdowns();
        vectorStore.doAdd(documents);
        return vectorStore;
    }
}

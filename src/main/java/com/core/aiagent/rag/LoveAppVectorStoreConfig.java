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
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    /**
     * SimpleVectorStore 是基于内存的简单向量存储
     * SimpleVectorStore 没有提供无参或可注入的构造器，所以只能这样实例化
     * SimpleVectorStore 实际只负责存储向量数据，而向量数据是通过 EmbeddingModel 将Document数据转换而来的
     * EmbeddingModel 是一个大模型的接口，具体实现类是 OpenAIEmbeddingModel
     */
    @Bean
    public VectorStore loveAppVectorStore(EmbeddingModel dashscoreEmbeddingModel) {

        // 需要传入一个大模型的EmbeddingModel，将文档转换为向量，实际保存的是向量类型的数据
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(dashscoreEmbeddingModel).build();

        // 加载数据
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        vectorStore.doAdd(documents);
        return vectorStore;
    }
}

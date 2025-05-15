package com.core.aiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 读取资源文件
 */
@Component
@Slf4j
public class LalaLianAppDocumentLoader {

    /**
     * 资源加载器,支持读取多个文件，支持通配符
     */
    private ResourcePatternResolver resourcePatternResolver;

    /**
     * 构造方法注入
     */
    public LalaLianAppDocumentLoader(ResourcePatternResolver resourcePatternResolver) {
        this.resourcePatternResolver = resourcePatternResolver;
    }

    /**
     * 加载markdown文件,转成Document对象
     */
    public List<Document> loadMarkdowns() {
        List<Document> list = new ArrayList<>();
        try {
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/lalalian/*.md");
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        // 当遇到 Markdown 中的水平分割线（如 `---`、`***`）时，拆分为新的 Document 实例
                        .withHorizontalRuleCreateDocument(true)
                        // 不在解析结果中保留区块引用（以 `>` 开头的内容）
                        .withIncludeBlockquote(false)
                        // 不在解析结果中保留代码块（以 ``` 包裹的内容）
                        .withIncludeCodeBlock(false)
                        .build();

                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                // 根据上面的config配置，读取markdown文件，得到一个拆分后的Document列表，用于后续的检索
                List<Document> documents = reader.get();
                list.addAll(documents);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }
}

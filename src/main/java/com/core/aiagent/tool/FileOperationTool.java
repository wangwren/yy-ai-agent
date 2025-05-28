package com.core.aiagent.tool;

import cn.hutool.core.io.FileUtil;
import com.core.aiagent.constant.FileConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 文件操作工具类
 * @Tool 表示这是一个工具类，可以被AI Agent调用
 * @ToolParam 表示工具方法的参数
 * description 用于描述工具方法或参数的功能，方便ai 理解和调用
 */
@Slf4j
public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "从文件中读取内容")
    public String readFile(@ToolParam(description = "要读取的文件名称") String fileName) {
        log.info("开始读取文件: {}", fileName);
        String filePath = FILE_DIR + "/" + fileName;
        try {
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e) {
            log.error("读取文件失败: {}", e.getMessage());
            return "Error reading file: " + e.getMessage();
        }
    }

    @Tool(description = "向文件中写入内容")
    public String writeFile(
        @ToolParam(description = "要写入的文件名称") String fileName,
        @ToolParam(description = "要写入的文件内容") String content) {
        log.info("开始写入文件: {}, 内容: {}", fileName, content);
        String filePath = FILE_DIR + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, filePath);
            return "File written successfully to: " + filePath;
        } catch (Exception e) {
            log.error("写入文件失败: {}", e.getMessage());
            return "Error writing to file: " + e.getMessage();
        }
    }
}

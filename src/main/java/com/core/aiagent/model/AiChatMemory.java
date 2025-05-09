package com.core.aiagent.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName ai_chat_memory
 */
@TableName(value ="ai_chat_memory")
@Data
public class AiChatMemory implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 会话id
     */
    @TableField("conversation_id")
    private String conversationId;

    /**
     * 消息类型
     */
    @TableField("type")
    private String type;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("is_delete")
    private Integer isDelete;

}
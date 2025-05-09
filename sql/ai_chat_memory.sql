CREATE TABLE ai_chat_memory (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    conversation_id VARCHAR(255) NOT NULL comment '会话id',
    type            VARCHAR(20)  NOT NULL comment '消息类型',
    content         TEXT         NOT NULL comment '消息内容',
    create_time      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    update_time      TIMESTAMP default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete        tinyint  default 0                 not null comment '是否删除',
    INDEX idx_conv (conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
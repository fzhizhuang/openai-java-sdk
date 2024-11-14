package cn.yishotech.openai.sdk.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 对话请求
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompletionRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String model;
    private String sessionId;
    private List<ChatMessage> messages;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ChatMessage {
        private String role;
        private String content;
    }
}

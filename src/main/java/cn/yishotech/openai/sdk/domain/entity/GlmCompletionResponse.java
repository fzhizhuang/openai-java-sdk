package cn.yishotech.openai.sdk.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 智谱对话响应
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Data
public class GlmCompletionResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private String model;
    private Long created;
    private List<Choice> choices;
    private Usage usage;

    @Data
    public static class Choice {
        private Message message;
        private Message delta;
        private Integer index;

        @JsonProperty("finish_reason")
        private String finishReason;

        @Data
        public static class Message {
            private String role;
            private String content;
        }
    }

    @Data
    public static class Usage {
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}

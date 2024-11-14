package cn.yishotech.openai.sdk.domain.entity;

import cn.yishotech.openai.sdk.domain.valobj.GlmModelVO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 智谱对话请求
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GlmCompletionRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private String model = GlmModelVO.GLM_4_FLASH.getCode();

    private List<Message> messages;

    @JsonProperty("request_id")
    private String requestId;

    @JsonProperty("do_sample")
    @Builder.Default
    private boolean doSample = true;

    @Builder.Default
    private boolean stream = false;

    @Builder.Default
    private Float temperature = 0.95f;

    @JsonProperty("top_p")
    @Builder.Default
    private Float topP = 0.7f;

    @JsonProperty("max_tokens")
    @Builder.Default
    private Integer maxTokens = 1024;


    private List<String> stop;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Message {
        private String role;
        private String content;
    }
}

package cn.yishotech.openai.sdk.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 星火对话响应
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SparkCompletionResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 错误码， 0-正常 非0-出错
     */
    private Integer code;

    /**
     * 会话是否成功的描述信息
     */
    private String message;

    /**
     * 会话的唯一ID，用于查看日志
     */
    private String sid;

    /**
     * 消息响应
     */
    private List<Choice> choices;

    /***
     * 消耗的token
     */
    private Usage usage;

    /**
     * Choice
     */
    @Data
    public static class Choice {
        /**
         * stream=false模式下返回
         */
        private Message message;

        /**
         * stream=true模式下返回
         */
        private Delta delta;

        /**
         * 结果序号,取值[0-10],当前为保留字段，开发者可以忽略
         */
        private Integer index;

        @Data
        public static class Message {
            /**
             * system-设置对话背景 | user-用户的问题 | assistant-AI的回复
             */
            private String role;

            // 用户和AI的对话内容
            private String content;
        }

        @Data
        public static class Delta {

            /**
             * 角色标识,固定为 assistant,标识角色为AI
             */
            private String role;

            /**
             * AI的回复
             */
            private String content;
        }
    }

    /**
     * 用量
     */
    @Data
    public static class Usage {

        /**
         * 历史问题消耗的tokens
         */
        @JsonProperty("prompt_tokens")
        private Integer promptTokens;

        /**
         * AI回答消耗的tokens
         */
        @JsonProperty("completion_tokens")
        private Integer completionTokens;

        /**
         * 本次交互的总消耗tokens
         */
        @JsonProperty("total_tokens")
        private Integer totalTokens;
    }
}

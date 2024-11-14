package cn.yishotech.openai.sdk.domain.entity;

import cn.yishotech.openai.sdk.domain.valobj.SparkModelVO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 星火对话请求
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SparkCompletionRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 对话模型
     */
    @Builder.Default
    private String model = SparkModelVO.SPARK_LITE.getCode();

    /**
     * 消息
     */
    private List<Message> messages;

    /**
     * 是否流式输出
     */
    @Builder.Default
    private boolean stream = false;

    /**
     * 核采样阈值。用于决定结果随机性，取值越高随机性越强即相同的问题得到的不同答案的可能性越高
     * 取值范围 [0,2] ，默认值1
     */
    @Builder.Default
    private Float temperature = 1F;


    /**
     * 模型回答的tokens的最大长度
     * Pro、Max、4.0 Ultra 取值为[1,8192]，默认为4096;
     * Lite、Pro-128K 取值为[1,4096]，默认为4096。
     */
    @JsonProperty("max_tokens")
    @Builder.Default
    private Integer maxTokens = 4096;

    /**
     * 从k个候选中随机选择⼀个（⾮等概率） top_k, [1-6]
     */
    @JsonProperty("top_k")
    @Builder.Default
    private Integer topK = 4;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Message {

        /**
         * 取值为[system,user,assistant]	system用于设置对话背景，user表示是用户的问题，assistant表示AI的回复
         */
        private String role;

        /**
         * 用户和AI的对话内容
         * 所有content的累计tokens需控制8192以内
         */
        private String content;
    }

}

package cn.yishotech.openai.sdk.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * openai配置参数
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "openai")
public class OpenaiProperties {

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "openai.glm")
    public static class GlmProperties {
        private String apiKey;
    }

    @Data
    @Configuration
    @ConfigurationProperties(prefix = "openai.spark")
    public static class SparkProperties {
        private String appId;
        private String apiKey;
        private String apiSecret;
    }
}

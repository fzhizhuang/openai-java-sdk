package cn.yishotech.openai.sdk.config;

import cn.yishotech.openai.sdk.domain.service.OpenaiClient;
import cn.yishotech.openai.sdk.domain.service.factory.OpenaiFactory;
import cn.yishotech.openai.sdk.domain.service.impl.GlmOpenaiService;
import cn.yishotech.openai.sdk.domain.service.impl.OpenaiClientImpl;
import cn.yishotech.openai.sdk.domain.service.impl.SparkOpenaiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * openai自动配置
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Configuration
public class OpenaiAutoConfiguration {

    @Bean
    public OpenaiProperties.GlmProperties glmProperties() {
        return new OpenaiProperties.GlmProperties();
    }

    @Bean
    public OpenaiProperties.SparkProperties sparkProperties() {
        return new OpenaiProperties.SparkProperties();
    }

    @Bean
    public OpenaiFactory openaiFactory() {
        return new OpenaiFactory(glmProperties(), sparkProperties());
    }

    @Bean
    public OpenaiClient openaiClient() {
        return new OpenaiClientImpl(openaiFactory());
    }

    @Bean
    public GlmOpenaiService glmOpenaiService() {
        return new GlmOpenaiService(glmProperties());
    }

    @Bean
    public SparkOpenaiService sparkOpenaiService() {
        return new SparkOpenaiService(sparkProperties());
    }
}

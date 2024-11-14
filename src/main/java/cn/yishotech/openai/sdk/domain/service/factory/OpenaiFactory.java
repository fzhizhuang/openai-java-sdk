package cn.yishotech.openai.sdk.domain.service.factory;

import cn.yishotech.openai.sdk.config.OpenaiProperties;
import cn.yishotech.openai.sdk.domain.service.OpenaiService;
import cn.yishotech.openai.sdk.domain.service.impl.GlmOpenaiService;
import cn.yishotech.openai.sdk.domain.service.impl.SparkOpenaiService;
import cn.yishotech.openai.sdk.domain.valobj.GlmModelVO;
import cn.yishotech.openai.sdk.domain.valobj.SparkModelVO;
import cn.yishotech.openai.sdk.types.exception.OpenaiException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * openai工厂
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenaiFactory {

    private final Map<String, OpenaiService> openaiServiceMap = new ConcurrentHashMap<>();

    private final OpenaiProperties.GlmProperties glmProperties;
    private final OpenaiProperties.SparkProperties sparkProperties;

    @PostConstruct
    public void init() {
        if (glmProperties != null) {
            for (GlmModelVO model : GlmModelVO.values()) {
                openaiServiceMap.put(model.getCode(), new GlmOpenaiService(glmProperties));
            }
            log.info("初始化智谱模型:{}", Arrays.stream(GlmModelVO.values()).map(GlmModelVO::getCode).toList());
        }
        if (sparkProperties != null) {
            for (SparkModelVO model : SparkModelVO.values()) {
                openaiServiceMap.put(model.getCode(), new SparkOpenaiService(sparkProperties));
            }
            log.info("初始化讯飞模型:{}", Arrays.stream(SparkModelVO.values()).map(SparkModelVO::getCode).toList());
        }
    }

    public OpenaiService getService(String model) {
        OpenaiService openaiService = openaiServiceMap.get(model);
        Optional.ofNullable(openaiService).orElseThrow(() -> new OpenaiException("未找到对应模型"));
        return openaiService;
    }
}

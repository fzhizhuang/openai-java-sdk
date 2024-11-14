package cn.yishotech.openai.sdk.domain.service.impl;

import cn.yishotech.openai.sdk.config.OpenaiProperties;
import cn.yishotech.openai.sdk.domain.entity.*;
import cn.yishotech.openai.sdk.domain.service.OpenaiService;
import cn.yishotech.openai.sdk.types.utils.OkhttpUtil;
import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 智谱服务实现
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GlmOpenaiService implements OpenaiService {

    private final OpenaiProperties.GlmProperties properties;

    public static final String COMPLETION_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    public static final String IMAGE_URL = "https://open.bigmodel.cn/api/paas/v4/images/generations";

    // 缓存
    public Cache<String, String> cache = Caffeine.newBuilder().expireAfterWrite(30, TimeUnit.MINUTES).build();

    @Override
    public void streamCompletion(CompletionRequest completionRequest, ResponseBodyEmitter emitter) {
        streamCompletion(completionRequest, emitter, new EventSourceListener() {
            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                log.info("数据：{}", data);
                if (data.equals("[DONE]")) {
                    emitter.complete();
                    return;
                }
                // 解析数据
                GlmCompletionResponse glmCompletionResponse = JSON.parseObject(data, GlmCompletionResponse.class);
                if (glmCompletionResponse != null) {
                    // 获取第一条数据
                    GlmCompletionResponse.Choice choice = glmCompletionResponse.getChoices().stream().findFirst().orElse(null);
                    if (choice != null) {
                        // 获取内容
                        String content = choice.getDelta().getContent();
                        // 发送数据
                        try {
                            emitter.send(content);
                        } catch (IOException e) {
                            log.error("发送数据失败", e);
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                log.info("连接关闭");
            }

            @Override
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                log.error("连接失败", t);
            }

            @Override
            public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
                log.info("连接成功");
            }
        });
    }

    @Override
    public void streamCompletion(CompletionRequest completionRequest, ResponseBodyEmitter emitter, EventSourceListener eventSourceListener) {
        // 参数校验
        if (completionRequest == null || emitter == null) {
            throw new IllegalArgumentException("参数异常");
        }
        // 转换参数
        GlmCompletionRequest glmCompletionRequest = buildGlmCompletionRequest(completionRequest);
        glmCompletionRequest.setStream(true);
        // 获取token
        String token = getToken();
        // 构建请求参数
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        OkHttpClient instance = OkhttpUtil.getInstance();
        Request request = OkhttpUtil.getRequest(COMPLETION_URL, JSON.toJSONString(glmCompletionRequest), headers);
        // 构建事件源工厂
        EventSource.Factory factory = EventSources.createFactory(instance);
        factory.newEventSource(request, eventSourceListener);
    }

    @Override
    public String completion(CompletionRequest completionRequest) {
        // 参数校验
        if (completionRequest == null) {
            throw new IllegalArgumentException("参数异常");
        }
        // 转换参数
        GlmCompletionRequest glmCompletionRequest = buildGlmCompletionRequest(completionRequest);
        glmCompletionRequest.setStream(true);
        // 获取token
        String token = getToken();
        // 构建请求参数
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        // 发送请求
        String post = OkhttpUtil.post(COMPLETION_URL, JSON.toJSONString(glmCompletionRequest), headers);
        // 处理数据
        if (StringUtils.isBlank(post)) return null;
        GlmCompletionResponse glmCompletionResponse = JSON.parseObject(post, GlmCompletionResponse.class);
        if (glmCompletionResponse != null) {
            // 获取第一条数据
            GlmCompletionResponse.Choice choice = glmCompletionResponse.getChoices().stream().findFirst().orElse(null);
            if (choice != null) return choice.getMessage().getContent();
        }
        return null;
    }

    @Override
    public String imageGenerate(ImageRequest imageRequest) {
        // 参数校验
        if (imageRequest == null) {
            throw new IllegalArgumentException("参数异常");
        }
        // 构建参数
        GlmImageRequest glmImageRequest = GlmImageRequest.builder().model(imageRequest.getModel()).prompt(imageRequest.getPrompt()).build();
        // 获取token
        String token = getToken();
        // 构建请求参数
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + token);
        // 发送请求
        String post = OkhttpUtil.post(IMAGE_URL, JSON.toJSONString(glmImageRequest), headers);
        if (StringUtils.isNotBlank(post)) {
            GlmImageResponse glmImageResponse = JSON.parseObject(post, GlmImageResponse.class);
            if (glmImageResponse != null) {
                // 获取第一条数据
                GlmImageResponse.Image image = glmImageResponse.getData().stream().findFirst().orElse(null);
                if (image != null) {
                    String url = image.getUrl();
                    // 生成base64
                    String base64 = generateBase64(url);
                    return parseBase64(base64);
                }
            }
        }
        return "";
    }

    /**
     * 构建智谱对话参数
     *
     * @param completionRequest 请求参数
     * @return 智谱对话参数
     */
    private static GlmCompletionRequest buildGlmCompletionRequest(CompletionRequest completionRequest) {
        return GlmCompletionRequest.builder().model(completionRequest.getModel())
                .messages(completionRequest.getMessages().stream().map(chatMessage -> GlmCompletionRequest.Message.builder().role(chatMessage.getRole()).content(chatMessage.getContent()).build()).toList())
                .build();
    }

    private String getToken() {
        // 获取配置
        Map<String, String> config = getConfig();
        // 获取缓存token
        String apiKey = config.get("apiKey");
        String apiSecret = config.get("apiSecret");
        // 获取token
        String token = cache.getIfPresent(apiKey);
        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        // 创建token
        Algorithm algorithm = Algorithm.HMAC256(apiSecret.getBytes(StandardCharsets.UTF_8));
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("api_key", apiKey);
        // 过期时间,默认30分钟
        int expireTime = 30;
        payload.put("exp", System.currentTimeMillis() + expireTime * 60 * 1000);
        payload.put("timestamp", Calendar.getInstance().getTimeInMillis());
        Map<String, Object> headerClaims = new HashMap<>();
        headerClaims.put("alg", "HS256");
        headerClaims.put("sign_type", "SIGN");
        token = JWT.create().withPayload(payload).withHeader(headerClaims).sign(algorithm);
        cache.put(apiKey, token);
        return token;
    }

    private Map<String, String> getConfig() {
        // 初始化智谱模型
        Map<String, String> map = new HashMap<>();
        String key = properties.getApiKey();
        // 解析密钥
        String apiKey, apiSecret;
        if (StringUtils.isBlank(key)) {
            log.warn("apiKey is null, please configure it before use.");
            throw new RuntimeException("apiKey is null, please configure it before use.");
        } else {
            String[] split = key.split("\\.");
            if (split.length != 2) {
                log.warn("apiKey format error, please check.");
                throw new RuntimeException("apiKey format error, please check.");
            }
            apiKey = split[0];
            apiSecret = split[1];
            map.put("apiKey", apiKey);
            map.put("apiSecret", apiSecret);
            return map;
        }
    }

    private String generateBase64(String url) {
        if (StringUtils.isBlank(url)) return null;
        // 生成base64
        return Base64.getEncoder().encodeToString(url.getBytes(StandardCharsets.UTF_8));
    }

    private String parseBase64(String base64) {
        return "data:image/png;base64," + base64;
    }
}

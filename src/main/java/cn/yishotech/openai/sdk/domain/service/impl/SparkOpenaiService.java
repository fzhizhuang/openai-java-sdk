package cn.yishotech.openai.sdk.domain.service.impl;

import cn.yishotech.openai.sdk.config.OpenaiProperties;
import cn.yishotech.openai.sdk.domain.entity.*;
import cn.yishotech.openai.sdk.domain.service.OpenaiService;
import cn.yishotech.openai.sdk.types.exception.OpenaiException;
import cn.yishotech.openai.sdk.types.utils.OkhttpUtil;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 星火openai服务实现
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SparkOpenaiService implements OpenaiService {

    private final OpenaiProperties.SparkProperties properties;

    private static final String COMPLETION_URL = "https://spark-api-open.xf-yun.com/v1/chat/completions";
    private static final String IMAGE_URL = "https://spark-api.cn-huabei-1.xf-yun.com/v2.1/tti";

    @Override
    public void streamCompletion(CompletionRequest completionRequest, ResponseBodyEmitter emitter) {
        streamCompletion(completionRequest, emitter, new EventSourceListener() {
            @Override
            public void onClosed(@NotNull EventSource eventSource) {
                log.info("连接关闭");
            }

            @Override
            public void onEvent(@NotNull EventSource eventSource, @Nullable String id, @Nullable String type, @NotNull String data) {
                log.info("数据：{}", data);
                if (data.equals("[DONE]")) {
                    emitter.complete();
                    return;
                }
                // 解析数据
                SparkCompletionResponse sparkCompletionResponse = JSON.parseObject(data, SparkCompletionResponse.class);
                if (sparkCompletionResponse != null) {
                    // 获取第一条数据
                    SparkCompletionResponse.Choice choice = sparkCompletionResponse.getChoices().stream().findFirst().orElse(null);
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
            public void onFailure(@NotNull EventSource eventSource, @Nullable Throwable t, @Nullable Response response) {
                log.error("连接失败", t);
            }

            @Override
            public void onOpen(@NotNull EventSource eventSource, @NotNull Response response) {
                log.info("连接成功");
            }
        });
    }

    @SneakyThrows
    @Override
    public void streamCompletion(CompletionRequest completionRequest, ResponseBodyEmitter emitter, EventSourceListener eventSourceListener) {
        // 参数校验
        if (completionRequest == null || emitter == null) {
            throw new IllegalArgumentException("参数异常");
        }
        // 转换参数
        SparkCompletionRequest sparkCompletionRequest = buildSparkCompletionRequest(completionRequest);
        sparkCompletionRequest.setStream(true);
        // 构建请求参数
        OkHttpClient instance = OkhttpUtil.getInstance();
        String url = getAuthUrl(COMPLETION_URL, properties.getApiKey(), properties.getApiSecret());
        Request request = OkhttpUtil.getRequest(url, JSON.toJSONString(sparkCompletionRequest), null);
        // 构建事件源工厂
        EventSource.Factory factory = EventSources.createFactory(instance);
        factory.newEventSource(request, eventSourceListener);
    }

    @SneakyThrows
    @Override
    public String completion(CompletionRequest completionRequest) {
        if (completionRequest == null) {
            throw new IllegalArgumentException("参数异常");
        }
        // 转换参数
        SparkCompletionRequest sparkCompletionRequest = buildSparkCompletionRequest(completionRequest);
        sparkCompletionRequest.setStream(true);
        // 构建请求参数
        String url = getAuthUrl(COMPLETION_URL, properties.getApiKey(), properties.getApiSecret());
        String post = OkhttpUtil.post(url, JSON.toJSONString(sparkCompletionRequest));
        if (StringUtils.isNotBlank(post)) {
            SparkCompletionResponse sparkCompletionResponse = JSON.parseObject(post, SparkCompletionResponse.class);
            if (sparkCompletionResponse != null) {
                // 获取第一条数据
                SparkCompletionResponse.Choice choice = sparkCompletionResponse.getChoices().stream().findFirst().orElse(null);
                if (choice != null) {
                    // 获取内容
                    return choice.getMessage().getContent();
                }
            }
        }
        return "";
    }

    @SneakyThrows
    @Override
    public String imageGenerate(ImageRequest imageRequest) {
        if (imageRequest == null) {
            throw new IllegalArgumentException("参数异常");
        }
        // 构建参数
        SparkImageRequest sparkImageGenerateRequest = buildSparkImageRequest(imageRequest);
        String url = getAuthUrl(IMAGE_URL, properties.getApiKey(), properties.getApiSecret());
        String post = OkhttpUtil.post(url, JSON.toJSONString(sparkImageGenerateRequest));
        if (StringUtils.isNotBlank(post)) {
            SparkImageResponse sparkImageResponse = JSON.parseObject(post, SparkImageResponse.class);
            if (sparkImageResponse != null) {
                // 获取第一条数据
                SparkImageResponse.Payload.Choice.Text text = sparkImageResponse.getPayload().getChoices().getText().stream().findFirst().orElse(null);
                if (text != null) {
                    // 获取base64
                    return parseBase64(text.getContent());
                }
            }
        }
        return "";
    }

    private SparkCompletionRequest buildSparkCompletionRequest(CompletionRequest completionRequest) {
        return SparkCompletionRequest.builder().model(completionRequest.getModel())
                .messages(completionRequest.getMessages().stream().map(chatMessage -> SparkCompletionRequest.Message.builder().role(chatMessage.getRole()).content(chatMessage.getContent()).build()).toList())
                .build();
    }

    /**
     * 获取url
     *
     * @return url
     * @throws Exception 异常
     */
    private String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        if (StringUtils.isBlank(apiKey) || StringUtils.isBlank(apiSecret)) {
            throw new OpenaiException("星火大模型apikey或apiSecret未配置,请配置后使用.");
        }
        URI uri = new URI(hostUrl);
        URL url = uri.toURL();
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // date="Thu, 12 Oct 2023 03:05:28 GMT";
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" + "date: " + date + "\n" + "POST " + url.getPath() + " HTTP/1.1";
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).
                addQueryParameter("date", date).
                addQueryParameter("host", url.getHost()).
                build();
        return httpUrl.toString();
    }

    /**
     * 将base64转换为base64 url
     *
     * @param base64 base64格式
     * @return url
     */
    private String parseBase64(String base64) {
        return "data:image/png;base64," + base64;
    }

    private SparkImageRequest buildSparkImageRequest(ImageRequest imageRequest) {
        return SparkImageRequest.builder()
                .header(SparkImageRequest.Header.builder()
                        .appId(properties.getAppId())
                        .build())
                .parameter(SparkImageRequest.Parameter.builder()
                        .chat(SparkImageRequest.Parameter.Chat.build(imageRequest.getModel(), SparkImageRequest.ImageSize.WH512x512))
                        .build())
                .payload(SparkImageRequest.Payload.builder()
                        .message(SparkImageRequest.Payload.Message.builder()
                                .text(Collections.singletonList(SparkImageRequest.Payload.Message.ImageText.builder()
                                        .role("user")
                                        .content(imageRequest.getPrompt())
                                        .build()))
                                .build())
                        .build())
                .build();
    }
}

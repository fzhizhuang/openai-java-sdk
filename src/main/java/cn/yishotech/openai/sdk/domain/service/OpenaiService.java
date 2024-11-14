package cn.yishotech.openai.sdk.domain.service;

import cn.yishotech.openai.sdk.domain.entity.CompletionRequest;
import cn.yishotech.openai.sdk.domain.entity.ImageRequest;
import okhttp3.sse.EventSourceListener;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * openai服务接口
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
public interface OpenaiService {

    /**
     * 流式调用
     *
     * @param completionRequest 请求参数
     * @param emitter           响应体
     */
    void streamCompletion(CompletionRequest completionRequest, ResponseBodyEmitter emitter);


    /**
     * 流式调用
     *
     * @param completionRequest   请求参数
     * @param emitter             响应体
     * @param eventSourceListener 事件监听器
     */
    void streamCompletion(CompletionRequest completionRequest, ResponseBodyEmitter emitter, EventSourceListener eventSourceListener);

    /**
     * 非流式调用
     *
     * @param completionRequest 请求参数
     * @return 对话响应
     */
    String completion(CompletionRequest completionRequest);

    /**
     * 图片生成
     *
     * @param imageRequest 请求参数
     * @return base64 url
     */
    String imageGenerate(ImageRequest imageRequest);
}

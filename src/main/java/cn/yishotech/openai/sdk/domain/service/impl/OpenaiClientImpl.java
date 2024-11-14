package cn.yishotech.openai.sdk.domain.service.impl;

import cn.yishotech.openai.sdk.domain.entity.CompletionRequest;
import cn.yishotech.openai.sdk.domain.entity.ImageRequest;
import cn.yishotech.openai.sdk.domain.service.OpenaiClient;
import cn.yishotech.openai.sdk.domain.service.factory.OpenaiFactory;
import lombok.RequiredArgsConstructor;
import okhttp3.sse.EventSourceListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

/**
 * openai客户端实现
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Service
@RequiredArgsConstructor
public class OpenaiClientImpl implements OpenaiClient {


    private final OpenaiFactory factory;

    @Override
    public void streamCompletion(CompletionRequest completionRequest, ResponseBodyEmitter emitter) {
        factory.getService(completionRequest.getModel())
                .streamCompletion(completionRequest, emitter);
    }

    @Override
    public void streamCompletion(CompletionRequest completionRequest, ResponseBodyEmitter emitter, EventSourceListener eventSourceListener) {
        factory.getService(completionRequest.getModel())
                .streamCompletion(completionRequest, emitter, eventSourceListener);
    }

    @Override
    public String completion(CompletionRequest completionRequest) {
        return factory.getService(completionRequest.getModel()).completion(completionRequest);
    }

    @Override
    public String imageGenerate(ImageRequest imageRequest) {
        return factory.getService(imageRequest.getModel()).imageGenerate(imageRequest);
    }
}

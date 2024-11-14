<h3 align="center">OpenAI SDK</h3>

## 介绍

OpenAI SDK 是一个基于智谱、星火等大模型的 Java SDK，用于实现智能对话、文本生成、文生图等功能。

### 使用

#### pom引入依赖

```xml

<dependency>
    <groupId>cn.yishotech</groupId>
    <artifactId>openai-java-sdk</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

#### 配置

```yaml
openai:
  glm:
    api-key: xxx.xxx
  spark:
    api-key: xxx
    api-secret: xxx
    app-id: xxx
```

### 使用

```java
@RequestMapping
@RestController
public class DemoController {

    @Resource
    private IOpenaiClient openaiClient;

    @PostMapping("/chat")
    public String chat(@RequestParam("model") String model, @RequestParam("prompt") String prompt) {
        CompletionRequest chatCompletionRequest = CompletionRequest.builder()
                .model(model)
                .messages(Collections.singletonList(CompletionRequest.ChatMessage.builder().role("user").content(prompt).build()))
                .build();
        return openaiClient.completion(chatCompletionRequest);
    }

    @NoRestful
    @PostMapping("/stream/chat")
    public ResponseBodyEmitter streamChat(@RequestParam("model") String model, @RequestParam("prompt") String prompt, HttpServletResponse response) {
        // 基础配置；流式输出、编码、禁用缓存
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        // 构建异步响应对象
        ResponseBodyEmitter emitter = new ResponseBodyEmitter(3 * 60 * 1000L);
        CompletionRequest chatCompletionRequest = CompletionRequest.builder()
                .model(model)
                .messages(Collections.singletonList(CompletionRequest.ChatMessage.builder().role("user").content(prompt).build()))
                .build();
        openaiClient.streamCompletion(chatCompletionRequest, emitter);
        return emitter;
    }

    @PostMapping("/image")
    public String image(@RequestParam("model") String model, @RequestParam("prompt") String prompt) {
        ImageRequest request = ImageRequest.builder()
                .model(model)
                .prompt(prompt)
                .build();
        return openaiClient.imageGenerate(request);
    }
}

```

## 许可证

根据 License 许可证分发。打开 [LICENSE](LICENSE) 查看更多内容。

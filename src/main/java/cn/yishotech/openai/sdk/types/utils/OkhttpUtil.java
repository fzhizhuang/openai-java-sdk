package cn.yishotech.openai.sdk.types.utils;

import okhttp3.*;

import java.io.IOException;
import java.net.Proxy;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * okhttp 工具类
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
public class OkhttpUtil {

    /**
     * 单例模式
     */
    private static volatile OkHttpClient instance;

    // 构建连接池
    public static ConnectionPool connectionPool = new ConnectionPool(10, 5, TimeUnit.MINUTES);

    // 获取实例
    public static OkHttpClient getInstance() {
        if (instance == null) {
            synchronized (OkhttpUtil.class) {
                if (instance == null) {
                    // 初始化实例
                    instance = new OkHttpClient.Builder()
                            .proxy(Proxy.NO_PROXY) // 设置代理
                            .connectionPool(connectionPool) // 设置连接池
                            .connectTimeout(1800, TimeUnit.SECONDS) // 设置连接超时时间
                            .readTimeout(1800, TimeUnit.SECONDS) // 设置读取超时时间
                            .writeTimeout(1800, TimeUnit.SECONDS) // 设置写入超时时间
                            .build();
                    // 设置最大并发数
                    instance.dispatcher().setMaxRequestsPerHost(200);
                    // 设置最大请求数
                    instance.dispatcher().setMaxRequests(200);
                }
            }
        }
        return instance;
    }

    /**
     * post 请求
     *
     * @param url  请求地址
     * @param json body 参数
     * @return 请求结果
     */
    public static String post(String url, String json) {
        return post(url, json, null);
    }

    /**
     * post 请求
     *
     * @param url     请求地址
     * @param json    body 参数
     * @param headers 请求头
     * @return 请求结果
     */
    public static String post(String url, String json, Map<String, String> headers) {
        MediaType mediaType = MediaType.Companion.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.Companion.create(json, mediaType);
        // 构建请求
        Request.Builder builder = new Request.Builder().url(url).method("POST", body).addHeader("Content-Type", "application/json");
        // 添加额外请求头
        Request request = addHeaders(builder, headers);
        // 发送请求
        return execute(request, instance);
    }

    /**
     * get 请求
     *
     * @param url     请求地址
     * @param json    body 参数
     * @param headers 请求头
     * @return 请求结果
     */
    public static String get(String url, String json, Map<String, String> headers) {
        MediaType mediaType = MediaType.Companion.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.Companion.create(json, mediaType);
        // 构建请求
        Request.Builder builder = new Request.Builder().url(url).method("GET", body).addHeader("Content-Type", "application/json");
        // 添加额外请求头
        Request request = addHeaders(builder, headers);
        // 发送请求
        return execute(request, instance);
    }

    /**
     * get 请求
     *
     * @param url  请求地址
     * @param json body 参数
     * @return 请求结果
     */
    public static String get(String url, String json) {
        return get(url, json, null);
    }

    /**
     * 添加请求头
     */
    private static Request addHeaders(Request.Builder builder, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    /**
     * 执行请求
     *
     * @param request  请求
     * @param instance okhttp 实例
     * @return 请求结果
     */
    private static String execute(Request request, OkHttpClient instance) {
        // 发送请求
        try {
            try (Response response = instance.newCall(request).execute()) {
                // 校验状态码
                if (!response.isSuccessful()) {
                    throw new RuntimeException("Unexpected code " + response);
                } else {
                    // 返回结果
                    if (response.body() != null) {
                        return response.body().string();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public static Request getRequest(String url, String json, Map<String, String> headers) {
        MediaType mediaType = MediaType.Companion.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.Companion.create(json, mediaType);
        // 构建请求
        Request.Builder builder = new Request.Builder().url(url).method("POST", body).addHeader("Content-Type", "application/json");
        // 添加额外请求头
        return addHeaders(builder, headers);
    }
}

package cn.yishotech.openai.sdk.domain.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 智谱文生图响应
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Data
public class GlmImageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String created;
    private List<Image> data;

    @Data
    public static class Image {
        private String url;
    }
}

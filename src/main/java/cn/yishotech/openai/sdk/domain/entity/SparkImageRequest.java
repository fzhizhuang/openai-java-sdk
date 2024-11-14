package cn.yishotech.openai.sdk.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 星火文生图请求
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SparkImageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 请求头
     */
    private Header header;

    /**
     * 请求参数
     */
    private Parameter parameter;

    /**
     * 请求体
     */
    private Payload payload;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Header {
        @JsonProperty("app_id")
        private String appId;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Parameter {

        private Chat chat;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        public static class Chat {

            private String domain;

            private Integer height;

            private Integer width;

            public static Chat build(String domain, ImageSize size) {
                return Chat.builder()
                        .domain(domain)
                        .height(size.getHeight())
                        .width(size.getWidth())
                        .build();
            }
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Payload {

        private Message message;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        public static class Message {

            private List<ImageText> text;

            @Data
            @AllArgsConstructor
            @NoArgsConstructor
            @Builder
            public static class ImageText {
                private String role;

                private String content;
            }
        }
    }

    @Getter
    @AllArgsConstructor
    public enum ImageSize {
        WH512x512(512, 512),
        WH640x360(640, 360),
        WH640x480(640, 480);
        private final Integer width;
        private final Integer height;
    }

}

package cn.yishotech.openai.sdk.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 星火消息响应
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SparkImageResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Header header;

    private Payload payload;

    @Data
    public static class Header {
        private Integer code;

        private String message;

        private String sid;

        private Integer status;
    }

    @Data
    public static class Payload {
        private Choice choices;

        @Data
        public static class Choice {
            private Integer status;

            private Integer seq;

            private List<Text> text;

            @Data
            public static class Text {
                private String content;

                private String role;

                private Integer index;
            }
        }
    }
}

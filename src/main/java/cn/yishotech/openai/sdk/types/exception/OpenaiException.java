package cn.yishotech.openai.sdk.types.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * openai异常
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OpenaiException extends RuntimeException {

    private Integer code;
    private String message;


    public OpenaiException(String message) {
        super(message);
        this.code = 3001;
        this.message = message;
    }
}

package cn.yishotech.openai.sdk.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 文生图请求
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String model;
    private String prompt;
}

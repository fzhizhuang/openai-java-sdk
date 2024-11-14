package cn.yishotech.openai.sdk.domain.entity;

import cn.yishotech.openai.sdk.domain.valobj.GlmModelVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 智谱文生图请求
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GlmImageRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String model = GlmModelVO.COGVIEW_3_PLUS.getCode();

    private String prompt;

    private String size;
}

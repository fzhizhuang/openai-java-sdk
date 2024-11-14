package cn.yishotech.openai.sdk.domain.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 智谱模型值对象
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Getter
@AllArgsConstructor
public enum GlmModelVO {

    // 对话模型
    GLM_4_PLUS("glm-4-plus", "高智能旗舰"),
    GLM_4_0520("glm-4-0520", "复杂推理"),
    GLM_4_LONG("glm-4-long", "长文本"),
    GLM_4_AIR("glm-4-air", "高性价比"),
    GLM_4_FLASH("glm-4-flash", "免费模型"),
    GLM_4_FLASH_X("glm-4-flashx", "高速低价，推理速度快"),
    // 绘画模型
    COGVIEW_3("cogview-3", "画图"),
    COGVIEW_3_PLUS("cogview-3-plus", "画图，价格更低"),
    ;
    private final String code;
    private final String desc;
}

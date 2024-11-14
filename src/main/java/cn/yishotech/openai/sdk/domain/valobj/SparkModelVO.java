package cn.yishotech.openai.sdk.domain.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 星火模型值对象
 *
 * @author zhizhuang
 * @date 2024/11/14
 */
@Getter
@AllArgsConstructor
public enum SparkModelVO {

    SPARK_LITE("general", "轻量级大语言模型,免费"),
    SPARK_PRO("generalv3", "专业级大语言模型,适用于文本、智能问答等对性能和响应速度有更高要求的业务场景"),
    SPARK_MAX("generalv3.5", "旗舰级大语言模型,适用于数理计算、逻辑推理等对效果有更高要求的业务场景"),
    SPARK_4_ULTRA("4.0Ultra", "最强大的大语言模型版本"),
    ;
    private final String code;
    private final String desc;
}

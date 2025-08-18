package com.bonss.system.config.sms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 读取项目相关配置
 *
 * @author bonss
 */
@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "aliyun")
public class AliyunSmsConfig {

    /**
     * id
     */
    private String smsAccessKeyId;

    /**
     * 密钥
     */
    private String smsAccessKeySecret;

    /**
     * 验证码模板code
     */
    private String smsVerifyTemplateCode;

    /**
     * 审核通过短信模板code
     */
    private String smsAuditApprovedTemplateCode;

    /**
     * 审核拒绝短信模板code
     */
    private String smsAuditRejectedTemplateCode;

    /**
     * 短信签名
     */
    private String smsSignName;

    /**
     * 发送短信最小间隔
     */
    private Integer smsMinInterval;

    /**
     * 是否开启短信发送,默认关闭
     */
    private boolean enable = false;
}

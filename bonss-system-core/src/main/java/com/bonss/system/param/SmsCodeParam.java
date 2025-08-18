package com.bonss.system.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * 短信验证码
 *
 * @author bonss
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsCodeParam {
    /**
     * 手机号码
     */
    @NotBlank(message = "手机号码不能为空")
    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    private String phoneNumber;

    /**
     * true不检查, false检查
     */
    private boolean skipPhoneCheck;

    /**
     * true是重置密码请求, false是其他请求,主要用于验证手机号
     */
    private boolean resetPwd;
}

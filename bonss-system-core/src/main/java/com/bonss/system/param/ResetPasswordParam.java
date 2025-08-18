package com.bonss.system.param;

import com.bonss.common.xss.Xss;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 忘记密码参数
 *
 * @author bonss
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordParam {
    /**
     * code
     */
    @Xss(message = "验证码不能包含脚本字符")
    @NotBlank(message = "验证码不能为空")
    @Size(min = 0, max = 6, message = "验证码长度不能超过6个字符")
    private String smsCode;


    /**
     * 手机号码
     */
    @NotBlank(message = "手机号码不能为空")
    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    private String phoneNumber;

    /**
     * 新密码
     */
    @Xss(message = "新密码不能包含脚本字符")
    @NotBlank(message = "新密码不能为空")
    @Size(min = 0, max = 20, message = "新密码长度不能超过20个字符")
    private String newPassword;

    /**
     * 确认新密码
     */
    @Xss(message = "确认新密码不能包含脚本字符")
    @NotBlank(message = "确认新密码不能为空")
    @Size(min = 0, max = 20, message = "确认新密码长度不能超过20个字符")
    private String confirmNewPassword;
}

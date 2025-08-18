package com.bonss.system.param;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

/**
 * 用户信息更新参数
 */
@Data
public class UpdateUserProfileParam {

    /**
     * 用户昵称
     */
    @NotBlank(message = "用户昵称不能为空")
    private String nickName;

    /**
     * 用户性别（0:男, 1:女, 2:未知）
     */
    @Pattern(regexp = "^[012]$", message = "用户性别不正确")
    private String sex;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phonenumber;
}

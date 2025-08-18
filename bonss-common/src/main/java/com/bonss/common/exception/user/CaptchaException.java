package com.bonss.common.exception.user;

/**
 * 验证码错误异常类
 * 
 * @author hzx
 */
public class CaptchaException extends UserException
{
    private static final long serialVersionUID = 1L;

    public CaptchaException()
    {
        super("图形验证码不匹配", null);
    }
}

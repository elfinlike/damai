package com.bonss.common.exception.user;

/**
 * 验证码失效异常类
 * 
 * @author hzx
 */
public class CaptchaExpireException extends UserException
{
    private static final long serialVersionUID = 1L;

    public CaptchaExpireException()
    {
        super("图形验证码过期", null);
    }
}

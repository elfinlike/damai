package com.bonss.common.constant;

/**
 * 缓存的key 常量
 * 
 * @author hzx
 */
public class CacheConstants
{
    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * App用户 redis key
     */
    public static final String APP_LOGIN_TOKEN_KEY = "app_login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

    /**
     * 登录用户信息缓存
     */
    public static final String REFRESH_TOKEN_KEY = "auth:rt:";

    /**
     * 登录用户最新token缓存
     */
    public static final String REFRESH_TOKEN_LATEST_KEY = "auth:rt:latest:";

    /**
     * 登录用户session缓存
     */
    public static final String USER_SESSIONS_KEY = "auth:user_sessions:";

    /**
     * 登录用户token黑名单缓存
     */
    public static final String AT_BLACKLIST_KEY = "auth:at:blacklist:";

    /**
     * 短信验证码 redis key
     */
    public static final String ALIYUN_SMS_CODE_KEY = "aliyun:sms:code:";

    /**
     * 重置密码时校验凭证 redis key
     */
    public static final String RESET_PWD_CERT_KEY = "reset:pwd:cert:";

    /**
     * 服务基础配置缓存 redis key
     */
    public static final String BASE_CONFIG_KEY = "config";

    /**
     * 栏目数据缓存 redis key
     */
    public static final String COLUMN_KEY = "column";
}

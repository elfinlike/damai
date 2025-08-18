package com.bonss.common.web.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;

import com.bonss.common.core.domain.model.LoginAdmin;
import com.bonss.common.enums.TokenType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.bonss.common.constant.CacheConstants;
import com.bonss.common.constant.Constants;
import com.bonss.common.core.redis.RedisCache;
import com.bonss.common.utils.ServletUtils;
import com.bonss.common.utils.StringUtils;
import com.bonss.common.utils.ip.AddressUtils;
import com.bonss.common.utils.ip.IpUtils;
import com.bonss.common.utils.uuid.IdUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * token验证处理
 *
 * @author hzx
 */
@Component
public class TokenService {
    private static final Logger log = LoggerFactory.getLogger(TokenService.class);

    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 短期令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime = 30;

    // 长期令牌有效期（默认30天，单位：天）
    @Value("${token.longExpireTime}")
    private int longExpireTime = 30;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    // 20分钟（用于令牌自动刷新判断）
    private static final Long MILLIS_MINUTE_TWENTY = 20 * 60 * 1000L;

    @Autowired
    private RedisCache redisCache;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginAdmin getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (StringUtils.isNotEmpty(token)) {
            try {
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                Integer tokenType = (Integer) claims.get(Constants.TOKEN_TYPE);
                if (tokenType == null) {
                    log.error("令牌中未包含token类型信息");
                    return null;
                }

                String userKey = getTokenKey(uuid);
                LoginAdmin user = redisCache.getCacheObject(userKey);
                if (user != null) {
                    user.setTokenType(tokenType); // 设置token类型
                }
                return user;
            } catch (Exception e) {
                log.error("获取用户信息异常: {}", e.getMessage());
            }
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginAdmin loginUser) {
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken())) {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = getTokenKey(token);
            redisCache.deleteObject(userKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @param tokenType 令牌类型（0：短期，1：长期）
     * @return 令牌
     */
    public String createToken(LoginAdmin loginUser, int tokenType) {
        // 校验token类型合法性
        if (tokenType != TokenType.SHORT_TOKEN.getCode() && tokenType != TokenType.LONG_TOKEN.getCode()) {
            throw new IllegalArgumentException("无效的token类型：" + tokenType);
        }

        String token = IdUtils.fastUUID();
        loginUser.setToken(token);
        loginUser.setTokenType(tokenType); // 存储token类型
        setUserAgent(loginUser);

        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        claims.put(Constants.JWT_USERNAME, loginUser.getUsername());
        claims.put(Constants.TOKEN_TYPE, tokenType); // 存入token类型（0或1）
        return createToken(claims);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser 登录信息
     */
    public void verifyToken(LoginAdmin loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE_TWENTY) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginAdmin loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        int tokenType = loginUser.getTokenType();
        long expireMillis;

        // 根据token类型计算过期时间
        if (tokenType == TokenType.SHORT_TOKEN.getCode()) {
            // 短期令牌：单位为分钟
            expireMillis = expireTime * MILLIS_MINUTE;
        } else if (tokenType == TokenType.LONG_TOKEN.getCode()) {
            // 长期令牌：单位为天（转换为分钟）
            expireMillis = longExpireTime * 24 * 60 * MILLIS_MINUTE;
        } else {
            throw new IllegalArgumentException("无效的token类型：" + tokenType);
        }

        loginUser.setExpireTime(loginUser.getLoginTime() + expireMillis);
        // 根据uuid将loginUser缓存，并设置对应过期时间
        String userKey = getTokenKey(loginUser.getToken());
        // 缓存时间单位统一为分钟（短期直接用expireTime，长期转换为分钟）
        int cacheTime = (tokenType == TokenType.SHORT_TOKEN.getCode()) ?
                expireTime :
                longExpireTime * 24 * 60;
        redisCache.setCacheObject(userKey, loginUser, cacheTime, TimeUnit.MINUTES);
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    public void setUserAgent(LoginAdmin loginUser) {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr();
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get(Constants.JWT_USERNAME);
    }

    /**
     * 获取请求token
     *
     * @param request 请求对象
     * @return token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String uuid) {
        return CacheConstants.LOGIN_TOKEN_KEY + uuid;
    }
}

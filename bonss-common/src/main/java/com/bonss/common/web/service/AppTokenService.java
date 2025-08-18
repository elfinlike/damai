package com.bonss.common.web.service;

import com.bonss.common.constant.CacheConstants;
import com.bonss.common.constant.Constants;
import com.bonss.common.core.domain.model.LoginUser;
import com.bonss.common.core.redis.RedisCache;
import com.bonss.common.utils.ServletUtils;
import com.bonss.common.utils.StringUtils;
import com.bonss.common.utils.ip.AddressUtils;
import com.bonss.common.utils.ip.IpUtils;
import com.bonss.common.utils.uuid.IdUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AppTokenService {

    @Value("${token.header}")
    private String header;

    @Value("${token.secret}")
    private String secret;

    @Value("${token.expireTime}")
    private int accessExpireMinutes = 30;      // 短期AT

    @Value("${token.longExpireTime}")
    private int refreshExpireDays = 30;     // RT总有效期(天)

    private static final long MILLIS_MINUTE = 60_000L;
    private static final long REFRESH_AHEAD_MILLIS = 20 * 60_000L; // AT滑动阈值20分钟

    @Autowired
    private RedisCache redisCache;

    /**
     * 生成AccessToken（JWT，带exp），并把LoginAdmin会话缓存到Redis（滑动续期依据）
     */
    public String createAccessToken(LoginUser loginUser) {
        // 会话ID（sid），沿用 LoginUser.token 字段（与老逻辑兼容）
        String sid = StringUtils.isEmpty(loginUser.getToken()) ? IdUtils.fastUUID() : loginUser.getToken();
        loginUser.setToken(sid);

        setUserAgent(loginUser);
        refreshSession(loginUser); // 写入 Redis：login_tokens:{sid}

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, sid);
        claims.put(Constants.JWT_USERNAME, loginUser.getUsername());
        claims.put(Constants.TOKEN_TYPE, 0); // AT 标记（可选）

        long now = System.currentTimeMillis();
        Date iat = new Date(now);
        Date exp = new Date(now + accessExpireMinutes * MILLIS_MINUTE);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(loginUser.getUsername())
                .setId(IdUtils.fastUUID())              // jti：用于黑名单
                .setIssuedAt(iat)
                .setExpiration(exp)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    /**
     * 从请求中解析AT并获取LoginUser（含滑动续期及黑名单检查）
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        String token = getToken(request);
        if (StringUtils.isEmpty(token)) return null;

        Claims claims;
        try {
            claims = parseToken(token);
        } catch (ExpiredJwtException e) {
            return null; // AT过期
        } catch (Exception e) {
            return null;
        }

        // 黑名单检查
        String jti = claims.getId();
        if (StringUtils.isNotEmpty(jti)) {
            String black = redisCache.getCacheObject(CacheConstants.AT_BLACKLIST_KEY + jti);
            if (StringUtils.isNotEmpty(black)) return null;
        }

        String sid = (String) claims.get(Constants.LOGIN_USER_KEY);
        if (StringUtils.isEmpty(sid)) return null;

        LoginUser user = redisCache.getCacheObject(getSessionKey(sid));
        if (user == null) return null;

        // 滑动续期：若Redis里的过期时间快到，则延长
        long remain = user.getExpireTime() - System.currentTimeMillis();
        if (remain <= REFRESH_AHEAD_MILLIS) {
            refreshSession(user);
        }
        return user;
    }

    /**
     * 写入/续期 Redis 会话（基于登录时间与AT有效期）
     */
    public void refreshSession(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        long expireMillis = accessExpireMinutes * MILLIS_MINUTE;
        loginUser.setExpireTime(loginUser.getLoginTime() + expireMillis);

        String key = getSessionKey(loginUser.getToken());
        redisCache.setCacheObject(key, loginUser, accessExpireMinutes, java.util.concurrent.TimeUnit.MINUTES);
    }

    /**
     * 将当前AT加入黑名单（用于强退/RT复用攻击）
     */
    public void blacklistAccessTokenJti(String jti, long ttlSeconds) {
        if (StringUtils.isEmpty(jti)) return;
        redisCache.setCacheObject(CacheConstants.AT_BLACKLIST_KEY + jti, "1", (int) ttlSeconds, java.util.concurrent.TimeUnit.SECONDS);
    }

    /**
     * 删除Redis中的会话（登出用）
     */
    public void deleteSessionBySid(String sid) {
        if (StringUtils.isEmpty(sid)) return;
        redisCache.deleteObject(getSessionKey(sid));
    }

    /**
     * 解析JWT
     */
    public Claims parseToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    /**
     * 从Header取token（形如 "Bearer xxx"）
     */
    public String getToken(HttpServletRequest request) {
        String token = request.getHeader(header);
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX)) {
            token = token.replace(Constants.TOKEN_PREFIX, "");
        }
        return token;
    }

    /**
     * 记录UA/IP等
     */
    public void setUserAgent(LoginUser loginUser) {
        UserAgent ua = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr();
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(ua.getBrowser().getName());
        loginUser.setOs(ua.getOperatingSystem().getName());
    }

    private String getSessionKey(String sid) {
        return CacheConstants.APP_LOGIN_TOKEN_KEY + sid;
    }
}

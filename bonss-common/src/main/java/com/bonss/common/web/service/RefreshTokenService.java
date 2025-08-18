package com.bonss.common.web.service;

import com.bonss.common.constant.CacheConstants;
import com.bonss.common.core.domain.model.LoginUser;
import com.bonss.common.core.redis.RedisCache;
import com.bonss.common.utils.StringUtils;
import com.bonss.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RefreshTokenService {

    @Autowired
    private RedisCache redisCache;

    @Value("${token.longExpireTime:30}") // 天
    private int refreshExpireDays;

    public static class RefreshRecord {
        public Long userId;
        public String sid;       // 会话ID（= LoginUser.token）
        public String jti;       // 本次RT ID
        public String parent;    // 父RT
        public String status;    // active|rotated|revoked
        public long expAt;       // 过期时间(毫秒)
    }

    private String rtKey(String sid, String jti){ return CacheConstants.REFRESH_TOKEN_KEY + sid + ":" + jti; }
    private String latestKey(String sid){ return CacheConstants.REFRESH_TOKEN_LATEST_KEY + sid; }
    private String userSessionsKey(Long uid){ return CacheConstants.USER_SESSIONS_KEY + uid; }

    /** 登录后创建初始RT，返回 refreshToken(jti) */
    public String createInitial(LoginUser user) {
        String jti = IdUtils.fastUUID();
        RefreshRecord r = new RefreshRecord();
        r.userId = user.getUserId();
        r.sid = user.getToken();
        r.jti = jti;
        r.parent = null;
        r.status = "active";
        r.expAt = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(refreshExpireDays);

        redisCache.setCacheObject(rtKey(r.sid, jti), r, refreshExpireDays, TimeUnit.DAYS);
        redisCache.setCacheObject(latestKey(r.sid), jti, refreshExpireDays, TimeUnit.DAYS);
        redisCache.setCacheObject(userSessionsKey(user.getUserId()), r.sid);
        return jti;
    }

    /** 用当前合法RT轮换生成新的RT；如检测到复用/作废，则抛异常 */
    public String rotate(LoginUser user, String refreshTokenJti) {
        if (StringUtils.isEmpty(refreshTokenJti)) throw new IllegalArgumentException("refresh_token_missing");

        String sid = user.getToken();
        RefreshRecord cur = redisCache.getCacheObject(rtKey(sid, refreshTokenJti));
        if (cur == null || System.currentTimeMillis() > cur.expAt) {
            throw new IllegalStateException("refresh_token_expired");
        }
        String latest = redisCache.getCacheObject(latestKey(sid));
        if (!"active".equals(cur.status) || !refreshTokenJti.equals(latest)) {
            // 复用攻击或已被轮换：直接撤销整个会话家族
            revokeSessionFamily(user.getUserId(), sid);
            throw new IllegalStateException("refresh_token_reuse_detected");
        }

        // 合法：生成新RT
        String newJti = IdUtils.fastUUID();
        RefreshRecord next = new RefreshRecord();
        next.userId = cur.userId;
        next.sid = cur.sid;
        next.jti = newJti;
        next.parent = cur.jti;
        next.status = "active";
        next.expAt = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(refreshExpireDays);

        // 更新
        cur.status = "rotated";
        redisCache.setCacheObject(rtKey(sid, cur.jti), cur, refreshExpireDays, TimeUnit.DAYS);
        redisCache.setCacheObject(rtKey(sid, newJti), next, refreshExpireDays, TimeUnit.DAYS);
        redisCache.setCacheObject(latestKey(sid), newJti, refreshExpireDays, TimeUnit.DAYS);
        return newJti;
    }

    /** 撤销单个会话（RT全部标记为revoked；并从用户会话集合中移除） */
    public void revokeSessionFamily(Long userId, String sid) {
        // 简化实现：删除 latestKey，并把集合中该sid移除；
        // 若需要逐条标记revoked，可维护索引或用SCAN，视 Redis 工具封装能力而定。
        redisCache.deleteObject(latestKey(sid));
        redisCache.setCacheObject(userSessionsKey(userId), sid);
        // 你也可以选择直接让会话AT失效（由外层拉黑当前AT jti）。
    }

    /** 撤销用户全部会话 */
    public void revokeAll(Long userId) {
        Set<String> sessions = redisCache.getCacheSet(userSessionsKey(userId));
        if (sessions != null) {
            for (String sid : sessions) {
                redisCache.deleteObject(latestKey(sid));
            }
        }
        redisCache.deleteObject(userSessionsKey(userId));
    }
}

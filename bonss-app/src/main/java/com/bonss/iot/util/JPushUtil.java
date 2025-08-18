package com.bonss.iot.util;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jpush.api.JPushClient;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Message;
import cn.jpush.api.push.model.Options;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.AndroidNotification;
import cn.jpush.api.push.model.notification.IosNotification;
import cn.jpush.api.push.model.notification.Notification;
import com.bonss.iot.config.JPushConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class JPushUtil {

    private final JPushClient jPushClient;
    private final JPushConfig jPushConfig;

    @Autowired
    public JPushUtil(JPushConfig jPushConfig) {
        this.jPushConfig = jPushConfig;
        this.jPushClient = new JPushClient(jPushConfig.getMasterSecret(),
                jPushConfig.getAppKey(),
                null,
                ClientConfig.getInstance());
    }

    /**
     * 向指定用户推送通知和消息
     * @param userId 用户ID
     * @param title 通知标题
     * @param content 通知内容
     * @param extras 额外参数
     * @return 推送结果
     */
    public boolean pushToUser(String userId, String title, String content, Map<String, String> extras) {
        try {
            PushPayload payload = buildPushPayload(userId, title, content, extras);
            PushResult result = jPushClient.sendPush(payload);
            log.info("极光推送成功，用户ID: {}, 推送ID: {}", userId, result.msg_id);
            return true;
        } catch (APIConnectionException e) {
            log.error("极光推送连接异常: {}", e.getMessage());
        } catch (APIRequestException e) {
            log.error("极光推送请求异常，HTTP状态码: {}, 错误码: {}, 错误信息: {}",
                    e.getStatus(), e.getErrorCode(), e.getErrorMessage());
        } catch (Exception e) {
            log.error("极光推送发生未知异常: {}", e.getMessage());
        }
        return false;
    }

    private PushPayload buildPushPayload(String userId, String title, String content, Map<String, String> extras) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.alias(userId)) // 使用用户ID作为别名
                .setNotification(Notification.newBuilder()
                        .setAlert(content)
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle(title)
                                .addExtras(extras)
                                .build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1)
                                .addExtras(extras)
                                .build())
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(content)
                        .setTitle(title)
                        .addExtras(extras)
                        .build())
                .setOptions(Options.newBuilder()
                        .setApnsProduction(jPushConfig.isProduction())
                        .build())
                .build();
    }
}

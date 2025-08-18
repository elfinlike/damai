package com.bonss.system.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bonss.common.constant.CacheConstants;
import com.bonss.common.constant.UserConstants;
import com.bonss.common.core.domain.entity.SysUser;
import com.bonss.common.core.redis.RedisCache;
import com.bonss.common.exception.ServiceException;
import com.bonss.common.utils.ExceptionUtil;
import com.bonss.system.config.sms.AliyunSmsConfig;
import com.bonss.system.mapper.SysUserMapper;
import com.bonss.system.param.SmsCodeParam;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 发送短信服务层
 *
 * @author wangjr
 */
@Component
@RequiredArgsConstructor
public class SmsService {

    private final static Logger log = LoggerFactory.getLogger(SmsService.class);

    private final AliyunSmsConfig aliyunSmsConfig;

    private final RedisCache redisCache;

    private final SysUserMapper sysUserMapper;

    //短信API产品名称（短信产品名固定，无需修改）
    private static final String PRODUCT = "Dysmsapi";

    //短信API产品域名（接口地址固定，无需修改）
    private static final String DOMAIN = "dysmsapi.aliyuncs.com";

    /**
     * 发送短信
     *
     * @param phoneNumber   手机号码
     * @param templateParam 短信模板参数
     */
    private void sendCodeSingleSms(String phoneNumber, String templateParam, String templateCode) {
        String smsKey = CacheConstants.ALIYUN_SMS_CODE_KEY + phoneNumber;
        try {
            if (!aliyunSmsConfig.isEnable()) {
                if (StrUtil.equals(templateCode, aliyunSmsConfig.getSmsVerifyTemplateCode())) {
                    redisCache.setCacheObject(smsKey, "123456", aliyunSmsConfig.getSmsMinInterval(), TimeUnit.MILLISECONDS);
                }
                log.info("短信服务模拟发送数据,phoneNumber: {},templateParam: {},templateCode: {}", phoneNumber, templateParam, templateCode);
                return;
            }
            //初始化ascClient,暂时不支持多region（请勿修改）
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou",
                    aliyunSmsConfig.getSmsAccessKeyId(), aliyunSmsConfig.getSmsAccessKeySecret());
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", PRODUCT, DOMAIN);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);
            //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为国际区号+号码，如“85200000000”
            request.setPhoneNumbers(phoneNumber);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(aliyunSmsConfig.getSmsSignName());
            //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
            request.setTemplateCode(templateCode);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            request.setTemplateParam(templateParam);
            //请求失败这里会抛ClientException异常
            SendSmsResponse response = acsClient.getAcsResponse(request);
            if (null != response && !StrUtil.equals("OK", response.getCode())) {
                log.error("[短信服务] 发送短信失败，手机号码：{} ，原因：{}", phoneNumber, response.getMessage());
                return;
            }
            if (StrUtil.equals(templateCode, aliyunSmsConfig.getSmsVerifyTemplateCode())) {
                log.info("[短信服务] 发送短信成功，手机号码：{} ，验证码：{}", phoneNumber, templateParam);
                redisCache.setCacheObject(smsKey, JSONUtil.parseObj(templateParam).get("code").toString(), aliyunSmsConfig.getSmsMinInterval(), TimeUnit.MILLISECONDS);
            }
        } catch (Exception e) {
            log.error("[短信服务] 发送短信异常，手机号码：{}", phoneNumber, e);
            throw new ServiceException("[短信服务] 发送短信异常!");
        }
    }

    /**
     * 发送验证码短信
     */
    public void sendCodeSingleSms(SmsCodeParam param) {
        SysUser sysUser = new SysUser();
        //直播小程序用户
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhonenumber, param.getPhoneNumber());
        sysUser = sysUserMapper.selectOne(queryWrapper);

        if (sysUser != null && StrUtil.equals(UserConstants.BAN_STATUS, sysUser.getStatus())) {
            throw new ServiceException("您的手机号绑定的账户存在异常行为,请联系我们解决!");
        }

        int first = RandomUtil.randomInt(1, 9);
        String templateParam = "{\"code\": " + first + RandomUtil.randomNumbers(5) + "}";
        sendCodeSingleSms(param.getPhoneNumber(), templateParam, aliyunSmsConfig.getSmsVerifyTemplateCode());
    }

    /**
     * 发送审核结果短信
     */
    public void sendSmsAudit(String phoneNumber, boolean isApproved) {
        SysUser sysUser = checkPhoneNumberExist(phoneNumber);
        if (sysUser == null) throw new ServiceException("发送短信失败，手机号不存在!");
        if (isApproved) {
            sendCodeSingleSms(phoneNumber, null, aliyunSmsConfig.getSmsAuditApprovedTemplateCode());
        } else {
            sendCodeSingleSms(phoneNumber, null, aliyunSmsConfig.getSmsAuditRejectedTemplateCode());
        }
    }

    /**
     * 校验短信验证码
     */
    public void validateSmsCode(String phoneNumber, String smsCode, Class<? extends RuntimeException> exceptionClass) {
        String smsKey = CacheConstants.ALIYUN_SMS_CODE_KEY + phoneNumber;
        String smsCodeFromCache = redisCache.getCacheObject(smsKey);
        if (StrUtil.isBlank(smsCodeFromCache)) {
            ExceptionUtil.throwException(exceptionClass, "短信验证码不存在或已过期,请重新获取!");
        }
        if (!smsCode.equals(smsCodeFromCache)) {
            ExceptionUtil.throwException(exceptionClass, "短信验证码错误");
        }
        redisCache.deleteObject(smsKey);
    }


    /**
     * 校验忘记密码的短信验证码
     * 返回凭证到前端,重置密码时携带凭证并校验,该凭证可以在注册时不再此验证手机号
     */
    public String validateResetPwdSmsCode(String phoneNumber, String smsCode) {
        validateSmsCode(phoneNumber, smsCode, ServiceException.class);
        String resetPwdCert = DigestUtil.md5Hex(smsCode + phoneNumber + IdUtil.fastUUID());
        String resetPwdCertKey = CacheConstants.RESET_PWD_CERT_KEY + resetPwdCert;
        redisCache.setCacheObject(resetPwdCertKey, phoneNumber, aliyunSmsConfig.getSmsMinInterval(), TimeUnit.MILLISECONDS);
        return resetPwdCert;
    }

    /**
     * 校验重置密码凭证
     */
    public String validateResetPwdCert(String cert) {
        String resetPwdCertKey = CacheConstants.RESET_PWD_CERT_KEY + cert;
        String phoneNumberFromCache = redisCache.getCacheObject(resetPwdCertKey);
        if (StrUtil.isBlank(phoneNumberFromCache)) {
            throw new ServiceException("重置密码凭证已过期,请重新验证手机号");
        }
        return phoneNumberFromCache;
    }

    private SysUser checkPhoneNumberExist(String phoneNumber) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPhonenumber, phoneNumber);
        return sysUserMapper.selectOne(queryWrapper);
    }
}

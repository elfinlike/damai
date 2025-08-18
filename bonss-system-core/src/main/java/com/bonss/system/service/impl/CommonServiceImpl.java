package com.bonss.system.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bonss.common.config.BonssConfig;
import com.bonss.common.constant.CacheConstants;
import com.bonss.common.core.redis.RedisCache;
import com.bonss.system.domain.SysConfig;
import com.bonss.system.domain.vo.BaseConfigVo;
import com.bonss.system.mapper.SysConfigMapper;
import com.bonss.system.service.ICommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonServiceImpl implements ICommonService, InitializingBean {

    private final static Logger log = LoggerFactory.getLogger(CommonServiceImpl.class);

    @Autowired
    private BonssConfig bonssConfig;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private SysConfigMapper configMapper;

    @Override
    public BaseConfigVo getConfig() {
        SysConfig baseConfig = configMapper.selectOne(null);
        return JSONUtil.toBean(baseConfig.getConfigValue(), BaseConfigVo.class);
    }

    @Override
    public BaseConfigVo updateBannerConfig(BaseConfigVo newConfig) {
        String value = JSONUtil.toJsonPrettyStr(newConfig);
        SysConfig build = SysConfig.builder()
                .configName("系统轮播图配置")
                .configKey("sys.banner")
                .configValue(value)
                .build();
        configMapper.updateConfig(build);
        return newConfig;
    }

    @Override
    public void afterPropertiesSet() {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, "sys.banner");
        SysConfig banner = configMapper.selectOne(wrapper);

        BaseConfigVo baseConfigVo = JSONUtil.toBean(banner.getConfigValue(), BaseConfigVo.class);
        if (!baseConfigVo.getInit()) return;

        baseConfigVo.setInit(false);
        // 清空原有配置
        SysConfig baseConfig = configMapper.selectOne(null);
        if (baseConfig != null) {
            configMapper.deleteById(baseConfig.getConfigId());
            redisCache.deleteObject(CacheConstants.BASE_CONFIG_KEY + "::default");
        }
        log.info("==================项目完成初始化配置加载!===========================");
    }
}

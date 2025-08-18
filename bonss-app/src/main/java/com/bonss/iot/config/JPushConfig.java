package com.bonss.iot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "jpush")
public class JPushConfig {
    private String appKey;
    private String masterSecret;
    private boolean production = false; // 默认使用测试环境
}

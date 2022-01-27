package com.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("swagger")
public class SwaggerProperties {
    /**
     * 是否开启swagger，生产环境一般关闭
     */
    private Boolean enable;

    /**
     * 项目名称
     */
    private String applicationName;

    /**
     * 项目版本
     */
    private String applicationVersion;

    /**
     * 项目描述信息
     */
    private String applicationDescription;

    /**
     * 接口调试地址
     */
    private String tryHost;
}
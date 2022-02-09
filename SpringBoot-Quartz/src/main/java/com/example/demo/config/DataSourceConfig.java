package com.example.demo.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.quartz.QuartzDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    private static final String BUSINESS_PREFIX = "spring.datasource.business";
    private static final String QUARTZ_PREFIX = "spring.datasource.quartz";

    private static HikariDataSource createHikariDataSource(DataSourceProperties properties) {
        // 创建 HikariDataSource 对象
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        if (StringUtils.hasText(properties.getName())) {
            dataSource.setPoolName(properties.getName());
        }
        return dataSource;
    }

    /**
     * 创建 business 数据源的配置对象
     */
    @Primary
    @Bean(name = "businessDataSourceProperties")
    @ConfigurationProperties(prefix = BUSINESS_PREFIX)
    public DataSourceProperties businessDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 创建 business 数据源的
     */
    @Primary
    @Bean(name = "businessDataSource")
    public DataSource businessDataSource() {
        // 获得 DataSourceProperties 对象
        DataSourceProperties properties = this.businessDataSourceProperties();
        // 创建 HikariDataSource 对象
        return createHikariDataSource(properties);
    }

    /**
     * 创建 quartz 数据源的配置对象
     */
    @Bean(name = "quartzDataSourceProperties")
    @ConfigurationProperties(prefix = QUARTZ_PREFIX)
    public DataSourceProperties quartzDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 创建 quartz 数据源
     */
    @Bean(name = "quartzDataSource")
    @QuartzDataSource
    public DataSource quartzDataSource() {
        // 获得 DataSourceProperties 对象
        DataSourceProperties properties = this.quartzDataSourceProperties();
        // 创建 HikariDataSource 对象
        return createHikariDataSource(properties);
    }
}

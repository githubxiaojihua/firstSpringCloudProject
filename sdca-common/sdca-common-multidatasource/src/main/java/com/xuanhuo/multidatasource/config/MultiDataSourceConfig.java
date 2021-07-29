package com.xuanhuo.multidatasource.config;

import com.xuanhuo.common.core.constant.DataSourceConstants;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
@PropertySource("classpath:multiDataSource.properties")
public class MultiDataSourceConfig {

    @Bean(DataSourceConstants.DS_KEY_SDFZ)
    @ConfigurationProperties(prefix = "spring.datasource.sdfz")
    public DataSource sdfzDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(DataSourceConstants.DS_KEY_GDATA3)
    @ConfigurationProperties(prefix = "spring.datasource.gdata3")
    public DataSource gdata3DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(DataSourceConstants.DS_KEY_YMFXYP)
    @ConfigurationProperties(prefix = "spring.datasource.ymfxyp")
    public DataSource ymfxypDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Primary
    public DataSource dynamicDataSource() {
        Map<Object, Object> dataSourceMap = new HashMap<>(2);
        dataSourceMap.put(DataSourceConstants.DS_KEY_SDFZ, sdfzDataSource());
        dataSourceMap.put(DataSourceConstants.DS_KEY_GDATA3, gdata3DataSource());
        dataSourceMap.put(DataSourceConstants.DS_KEY_YMFXYP, ymfxypDataSource());
        //设置动态数据源
        MultiDataSource dynamicDataSource = new MultiDataSource();
        dynamicDataSource.setTargetDataSources(dataSourceMap);
        //默认数据源
        dynamicDataSource.setDefaultTargetDataSource(sdfzDataSource());
        return dynamicDataSource;
    }
}
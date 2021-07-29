package com.xuanhuo.multidatasource.annotation;

import com.xuanhuo.common.core.constant.DataSourceConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultiDataSource {
    /**
     * 数据源名称
     * @return
     */
    String value() default DataSourceConstants.DS_KEY_SDFZ;
}

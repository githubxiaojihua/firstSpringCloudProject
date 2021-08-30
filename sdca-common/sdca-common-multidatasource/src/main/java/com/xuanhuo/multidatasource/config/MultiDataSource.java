package com.xuanhuo.multidatasource.config;

import com.xuanhuo.common.core.constant.DataSourceConstants;
import com.xuanhuo.multidatasource.context.MultiDataSourceContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源类
 */
public class MultiDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return MultiDataSourceContextHolder.getContextKey();
    }

}

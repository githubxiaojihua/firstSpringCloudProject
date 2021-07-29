package com.xuanhuo.multidatasource.config;

import com.xuanhuo.common.core.constant.DataSourceConstants;
import com.xuanhuo.multidatasource.context.MultiDataSourceContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MultiDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return MultiDataSourceContextHolder.getContextKey();
    }

}

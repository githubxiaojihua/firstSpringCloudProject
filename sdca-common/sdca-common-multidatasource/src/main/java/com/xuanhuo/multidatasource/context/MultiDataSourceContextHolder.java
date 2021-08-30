package com.xuanhuo.multidatasource.context;

import com.xuanhuo.common.core.constant.DataSourceConstants;

/**
 * 当前数据源 上下文
 * 通过当前线程保存设置的数据源
 */
public class MultiDataSourceContextHolder {

    private static final ThreadLocal<String> MULTIDATASOURCE_CONTEXT_KEY_HOLDER = new ThreadLocal<>();

    /**
     * 设置数据源
     * @param key
     */
    public static void setContextKey(String key){
        MULTIDATASOURCE_CONTEXT_KEY_HOLDER.set(key);
    }

    /**
     * 获取数据源名称
     * @return
     */
    public static String getContextKey(){
        String key = MULTIDATASOURCE_CONTEXT_KEY_HOLDER.get();
        return key == null? DataSourceConstants.DS_KEY_SDFZ:key;
    }

    /**
     * 删除当前数据源名称
     */
    public static void removeContextKey(){
        MULTIDATASOURCE_CONTEXT_KEY_HOLDER.remove();
    }
}

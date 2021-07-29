package com.xuanhuo.common.core.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapUtil {

    /**
     * 将oldData中的key，换成data1,data2......
     * 主要用于报表系统
     * @param oldData
     * @param newData
     */
    public static void exchangeReportDate(Map<String,Object> oldData,Map<String,Object> newData){
        oldData.remove("日期");
        int index = 1;
        for(Object value : oldData.values()){
            newData.put("data"+index++,value);
        }
    }
}
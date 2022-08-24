package com.xuanhuo.common.core.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapUtil {

    /**
     * 将oldData中的key，换成data1,data2......
     * 主要用于报表系统
     * @param oldData
     * @param newData
     */
    public static void exchangeReportDate(Map<String,Object> oldData,Map<String,Object> newData){
        //初始化newData
        if(newData.entrySet().size() == 0){
            newData.put("data1",0);
            newData.put("data2",0);
            newData.put("data3",0);
            newData.put("data4",0);
            newData.put("data5",0);
            newData.put("data6",0);
            newData.put("data7",0);
        }
        oldData.remove("日期");
        int index = 1;
        for(Object value : oldData.values()){
            newData.put("data"+index++,value);
        }
    }

    /**
     * 将查询结果根据日期对应到data1,data2,data3....
     * colData需要有dt(日期)及num(数值)两列
     * 主要用于报表系统
     * @param colData
     * @param dataX
     */
        public static void exchangeCol2DataXByDate(List<Map<String,String>> colData, Map<String,String> dataX, String ksrqStr){
        //初始化newData
        if(dataX.entrySet().size() == 0){
            dataX.put("data1","0");
            dataX.put("data2","0");
            dataX.put("data3","0");
            dataX.put("data4","0");
            dataX.put("data5","0");
            dataX.put("data6","0");
            dataX.put("data7","0");
        }
        DateTime ksrq = DateUtil.parse(ksrqStr, "yyyyMMdd");
        for(Map<String,String> value : colData){
            if(DateUtil.compare(DateUtil.parse(value.get("dt")),ksrq) == 0){
                dataX.put("data1",value.get("num"));
            }
            if(DateUtil.compare(DateUtil.parse(value.get("dt")),DateUtil.offsetDay(ksrq,1)) == 0){
                dataX.put("data2",value.get("num"));
            }
            if(DateUtil.compare(DateUtil.parse(value.get("dt")),DateUtil.offsetDay(ksrq,2)) == 0){
                dataX.put("data3",value.get("num"));
            }
            if(DateUtil.compare(DateUtil.parse(value.get("dt")),DateUtil.offsetDay(ksrq,3)) == 0){
                dataX.put("data4",value.get("num"));
            }
            if(DateUtil.compare(DateUtil.parse(value.get("dt")),DateUtil.offsetDay(ksrq,4)) == 0){
                dataX.put("data5",value.get("num"));
            }
            if(DateUtil.compare(DateUtil.parse(value.get("dt")),DateUtil.offsetDay(ksrq,5)) == 0){
                dataX.put("data6",value.get("num"));
            }
            if(DateUtil.compare(DateUtil.parse(value.get("dt")),DateUtil.offsetDay(ksrq,6)) == 0){
                dataX.put("data7",value.get("num"));
            }
        }
    }
}

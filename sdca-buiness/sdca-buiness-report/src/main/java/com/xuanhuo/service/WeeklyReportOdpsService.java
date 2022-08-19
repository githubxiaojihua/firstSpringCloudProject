package com.xuanhuo.service;


import com.xuanhuo.pojo.StaticDate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface WeeklyReportOdpsService {
    void callSqlFunction(Map param);

    Future<List<Map<String, Object>>> getSDFZSqlResult(String sql);

    Future<List<Map<String, Object>>> getSDFZLogData(String ksrq,String jsrq);

    Future<List<Map<String, Object>>> getGdata3SqlResult(String sql);

    Future<List<Map<String, Object>>> getOdpsSqlResult(String sql);

    Future<List<Map<String, Object>>> getYMFXYPSqlResult(String sql);

    Future<List<Map<String, String>>> select4weekwarn(StaticDate staticDate);

    Future<Map<String, String>> getHybbzzl(String ksrq,String jsrq);

    Future<Map<String, String>> getHybkljzl();

    Future<List<Map<String, String>>> getHybkFourWeek(String ksrq,String jsrq);

    Future<List<Map<String, String>>> select4weekwarnTrend(StaticDate staticDate);

    Future<List<Map<String, String>>> getLogQuality(String rq);


}

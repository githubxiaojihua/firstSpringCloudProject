package com.xuanhuo.service;


import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface WeeklyReportService {
    void callSqlFunction(Map param);

    Future<List<Map<String, Object>>> getSDFZSqlResult(String sql);

    Future<List<Map<String, Object>>> getGdata3SqlResult(String sql);

    Future<List<Map<String, Object>>> getHiveSqlResult(String sql);

    Future<List<Map<String, Object>>> getYMFXYPSqlResult(String sql);
}

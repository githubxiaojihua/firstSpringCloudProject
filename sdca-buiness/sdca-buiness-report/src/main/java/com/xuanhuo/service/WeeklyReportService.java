package com.xuanhuo.service;


import java.util.List;
import java.util.Map;

public interface WeeklyReportService {
    void callSqlFunction(Map param);

    List<Map<String, Object>> getSDFZSqlResult(String sql);

    List<Map<String, Object>> getGdata3SqlResult(String sql);
}

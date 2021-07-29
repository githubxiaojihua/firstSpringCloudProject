package com.xuanhuo.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 周报
 * java----python接口对象
 */
@Data
public class WeeklyReportResult {
    private String month;
    private String day;
    private String start_date;
    private String end_date;
    private List<Map<String,Object>> table = new ArrayList<>();

    public WeeklyReportResult(StaticDate staticDate){
        this.setMonth(staticDate.getMonth());
        this.setDay(staticDate.getDay());
        this.setStart_date(staticDate.getWarnStartDate());
        this.setEnd_date(staticDate.getWarnEndDate());
    }
}

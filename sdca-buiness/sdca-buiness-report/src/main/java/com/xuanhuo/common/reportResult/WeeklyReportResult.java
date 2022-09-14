package com.xuanhuo.common.reportResult;

import com.xuanhuo.pojo.StaticDate;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * 周报结果对象
 * java----python接口对象
 */
@Data
public class WeeklyReportResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private String month;
    private String day;
    private String start_date;
    private String end_date;
    private String week;
    private String zd;

    private String app;
    private String net;

    private Map<String,Map<String,String>> qq_wx;

    private Map<String,String> lt = new LinkedHashMap<>();
    private Map<String,String> yd = new LinkedHashMap<>();
    private Map<String,String> dx = new LinkedHashMap<>();

    private String hlwrzsjzl;

    private List<Map<String,Object>> table = new ArrayList<>();
    private List<Map<String,Object>> table_week = new ArrayList<>();

    private Map<String,Object> quality = new LinkedHashMap<>();

    public WeeklyReportResult(){}

}

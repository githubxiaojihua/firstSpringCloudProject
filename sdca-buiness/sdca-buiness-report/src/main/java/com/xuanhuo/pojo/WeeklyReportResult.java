package com.xuanhuo.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * 周报
 * java----python接口对象
 */
@Data
public class WeeklyReportResult implements Serializable {
    private static final long serialVersionUID = 1L;

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

        //贷款、代办信用卡类
        Map<String,Object> dk_dbxykl = new LinkedHashMap<>();
        dk_dbxykl.put("name","贷款、代办信用卡类");
        dk_dbxykl.put("website",null);
        dk_dbxykl.put("warning",null);
        dk_dbxykl.put("website_last_week",null);
        dk_dbxykl.put("warning_last_week",null);

        //刷单返利类
        Map<String,Object> sdfll = new LinkedHashMap<>();
        sdfll.put("name","刷单返利类");
        sdfll.put("website",null);
        sdfll.put("warning",null);
        sdfll.put("website_last_week",null);
        sdfll.put("warning_last_week",null);

        //杀猪盘类
        Map<String,Object> szpl = new LinkedHashMap<>();
        szpl.put("name","杀猪盘类");
        szpl.put("website",null);
        szpl.put("warning",null);
        szpl.put("website_last_week",null);
        szpl.put("warning_last_week",null);
        szpl.put("website_category",null);
        szpl.put("warning_category",null);
        szpl.put("website_category_last_week",null);
        szpl.put("warning_category_lat_week",null);

        //虚假ETC专项
        Map<String,Object> xjETC = new LinkedHashMap<>();
        xjETC.put("name","虚假ETC专项");
        xjETC.put("website",null);
        xjETC.put("warning",null);
        xjETC.put("message",null);
        xjETC.put("website_last_week",null);
        xjETC.put("warning_last_week",null);
        xjETC.put("message",null);

        //公安提供的其他类型诈骗网站及预警
        Map<String,Object> gaqtl = new LinkedHashMap<>();
        gaqtl.put("name","公安提供的其他类型诈骗网站及预警");
        gaqtl.put("website",null);
        gaqtl.put("warning",null);
        gaqtl.put("website_last_week",null);
        gaqtl.put("warning_last_week",null);

        //各类型预警按地市统计
        Map<String,Object> glxyjadstj = new LinkedHashMap<>();
        glxyjadstj.put("name","各类型预警按地市统计");
        glxyjadstj.put("last_week_warning",null);
        glxyjadstj.put("city",null);

        table.add(dk_dbxykl);
        table.add(sdfll);
        table.add(szpl);
        table.add(xjETC);
        table.add(gaqtl);
        table.add(glxyjadstj);
    }
}

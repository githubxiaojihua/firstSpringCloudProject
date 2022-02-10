package com.xuanhuo.common.reportResult;

import com.xuanhuo.pojo.StaticDate;
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
    private String week;

    private Map<String,String> lt = new LinkedHashMap<>();
    private Map<String,String> yd = new LinkedHashMap<>();
    private Map<String,String> dx = new LinkedHashMap<>();

    private List<Map<String,Object>> table = new ArrayList<>();
    private List<Map<String,Object>> table_week = new ArrayList<>();

    private Map<String,Object> quality = new LinkedHashMap<>();

    public WeeklyReportResult(){}

    public WeeklyReportResult(StaticDate staticDate){
        this.setMonth(staticDate.getMonth());
        this.setDay(staticDate.getDay());
        this.setStart_date(staticDate.getWarnStartDate());
        this.setEnd_date(staticDate.getWarnEndDate());
        this.setWeek(staticDate.getWeek());

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

        //预警数据近四周变化趋势统计
        Map<String,Object> yjsjjszbhqstj = new LinkedHashMap<>();
        yjsjjszbhqstj.put("name","预警数据近四周变化趋势统计");
        yjsjjszbhqstj.put("header_name","预警总数");
        yjsjjszbhqstj.put("week1",null);
        yjsjjszbhqstj.put("week2",null);
        yjsjjszbhqstj.put("week3",null);
        yjsjjszbhqstj.put("week4",null);

        //黑样本库统计分析
        Map<String,Object> hybktjfx = new LinkedHashMap<>();
        hybktjfx.put("name","黑样本库统计分析");
        hybktjfx.put("header_name","涉诈网站数");
        hybktjfx.put("history_sum",null);
        hybktjfx.put("week_sum",null);
        hybktjfx.put("week1",null);
        hybktjfx.put("week2",null);
        hybktjfx.put("week3",null);
        hybktjfx.put("week4",null);

        //预警数据中近四周的活跃网站数量统计
        Map<String,Object> yjsjzjszdhywzsltj = new LinkedHashMap<>();
        yjsjzjszdhywzsltj.put("name","预警数据中近四周的活跃网站数量统计");
        yjsjzjszdhywzsltj.put("header_name","活跃涉诈网站");
        yjsjzjszdhywzsltj.put("week1",null);
        yjsjzjszdhywzsltj.put("week2",null);
        yjsjzjszdhywzsltj.put("week3",null);
        yjsjzjszdhywzsltj.put("week4",null);

        table_week.add(yjsjjszbhqstj);
        table_week.add(hybktjfx);
        table_week.add(yjsjzjszdhywzsltj);

        //日志数据初始化，防止源数据丢失导致出现传递给data1-----data7之间任何数据缺失
        lt.put("data1","0");
        lt.put("data2","0");
        lt.put("data3","0");
        lt.put("data4","0");
        lt.put("data5","0");
        lt.put("data6","0");
        lt.put("data7","0");

        yd.put("data1","0");
        yd.put("data2","0");
        yd.put("data3","0");
        yd.put("data4","0");
        yd.put("data5","0");
        yd.put("data6","0");
        yd.put("data7","0");

        dx.put("data1","0");
        dx.put("data2","0");
        dx.put("data3","0");
        dx.put("data4","0");
        dx.put("data5","0");
        dx.put("data6","0");
        dx.put("data7","0");

        quality.put("date",staticDate.getLogQuaDate());
        quality.put("total",null);
        quality.put("host_not_null",null);
        quality.put("ssl_sni_not_null",null);


    }
}

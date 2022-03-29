package com.xuanhuo.pojo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 统计时间
 */
@Data
public class StaticDate {
    private String staticDate;
    private String warnStartDate;
    private String warnEndDate;
    private String websitStartDate;
    private String websitEndDate;
    //报表中统计日期
    private String month;
    private String day;
    private String week;

    //近四周时间
    private String week1Start;
    private String week1End;
    private String week2Start;
    private String week2End;
    private String week3Start;
    private String week3End;
    private String week4Start;
    private String week4End;

    //日志质量统计日期为统计周期的第三天
    private String logQuaDate;

    //日志中所有表格的日期列表
    private List<String> dateList = new ArrayList<>();

    /**
     * 根据统计日期 计算具体统计日期
     * @param staticDate
     */
    public StaticDate(String staticDate){
        this.staticDate = staticDate;
        //默认为当前日期
        Date staticData = DateUtil.date();

        if(StrUtil.isNotEmpty(staticDate)){
            staticData = DateUtil.parse(staticDate,"yyyyMMdd");
        }
        //统计周期的第三天
        this.setLogQuaDate(DateUtil.format(DateUtil.offsetDay(staticData,-5),"yyyy-MM-dd"));
        this.setWarnStartDate(DateUtil.format(DateUtil.offsetDay(staticData,-7),"yyyyMMdd"));
        this.setWarnEndDate(DateUtil.format(DateUtil.offsetDay(staticData,-1),"yyyyMMdd"));
        this.setWebsitStartDate(DateUtil.format(DateUtil.offsetDay(staticData,-8),"yyyyMMdd"));
        this.setWebsitEndDate(DateUtil.format(DateUtil.offsetDay(staticData,-2),"yyyyMMdd"));

        this.setMonth(DateUtil.format(staticData,"MM"));
        this.setDay(DateUtil.format(staticData,"dd"));
        this.setWeek(String.valueOf(DateUtil.weekOfYear(DateUtil.parse(warnStartDate,"yyyyMMdd")) -1));

        this.setWeek1Start(DateUtil.format(DateUtil.offsetWeek(staticData,-4),"yyyyMMdd"));
        this.setWeek1End(DateUtil.format(DateUtil.offsetDay(DateUtil.parse(week1Start,"yyyyMMdd"),6),"yyyyMMdd"));
        this.setWeek2Start(DateUtil.format(DateUtil.offsetWeek(staticData,-3),"yyyyMMdd"));
        this.setWeek2End(DateUtil.format(DateUtil.offsetDay(DateUtil.parse(week2Start,"yyyyMMdd"),6),"yyyyMMdd"));
        this.setWeek3Start(DateUtil.format(DateUtil.offsetWeek(staticData,-2),"yyyyMMdd"));
        this.setWeek3End(DateUtil.format(DateUtil.offsetDay(DateUtil.parse(week3Start,"yyyyMMdd"),6),"yyyyMMdd"));
        this.setWeek4Start(DateUtil.format(DateUtil.offsetWeek(staticData,-1),"yyyyMMdd"));
        this.setWeek4End(DateUtil.format(DateUtil.offsetDay(DateUtil.parse(week4Start,"yyyyMMdd"),6),"yyyyMMdd"));

        Date warnStartDate = DateUtil.parse(this.warnStartDate);
        Date warnEndDate = DateUtil.parse(this.warnEndDate);
        while(DateUtil.compare(warnStartDate,warnEndDate)!=1){
            this.dateList.add(DateUtil.format(warnStartDate,"yyyyMMdd"));
            warnStartDate = DateUtil.offsetDay(warnStartDate,1);
        }

    }

}

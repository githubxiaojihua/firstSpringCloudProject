package com.xuanhuo.pojo;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.Date;

/**
 * 统计时间
 */
@Data
public class StaticDate {
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

    /**
     * 根据统计日期 计算具体统计日期
     * @param staticDate
     */
    public StaticDate(String staticDate){
        //默认为当前日期
        Date staticData = DateUtil.date();
        if(StrUtil.isNotEmpty(staticDate)){
            staticData = DateUtil.parse(staticDate,"yyyyMMdd");
        }
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
    }

}

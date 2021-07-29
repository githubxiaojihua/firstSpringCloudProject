package com.xuanhuo.pojo;

import cn.hutool.core.date.DateUtil;
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

    /**
     * 根据统计日期 计算具体统计日期
     * @param staticDate
     */
    public StaticDate(String staticDate){
        //默认为当前日期
        Date staticData = DateUtil.date();
        if(StrUtil.isNotEmpty(staticDate)){
            staticData = DateUtil.parseDate(staticDate);
        }
        this.setWarnStartDate(DateUtil.format(DateUtil.offsetDay(staticData,-7),"yyyyMMdd"));
        this.setWarnEndDate(DateUtil.format(DateUtil.offsetDay(staticData,-1),"yyyyMMdd"));
        this.setWebsitStartDate(DateUtil.format(DateUtil.offsetDay(staticData,-8),"yyyyMMdd"));
        this.setWebsitEndDate(DateUtil.format(DateUtil.offsetDay(staticData,-2),"yyyyMMdd"));

        this.setMonth(DateUtil.format(staticData,"MM"));
        this.setDay(DateUtil.format(staticData,"dd"));
    }

}

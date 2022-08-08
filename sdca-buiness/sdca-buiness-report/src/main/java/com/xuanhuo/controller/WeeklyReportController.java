package com.xuanhuo.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataUnit;
import cn.hutool.core.util.StrUtil;
import com.xuanhuo.common.reportResult.IWeeklyReportResultBuilder;
import com.xuanhuo.common.reportResult.IWeeklyReportResultDirector;
import com.xuanhuo.common.reportResult.WeeklyReportResultBuilderImpl;
import com.xuanhuo.common.reportResult.WeeklyReportResultDirectorImpl;
import com.xuanhuo.common.core.controller.BaseController;
import com.xuanhuo.common.core.domain.AjaxResult;
import com.xuanhuo.common.core.utils.SerializableUtil;
import com.xuanhuo.pojo.StaticDate;
import com.xuanhuo.common.reportResult.WeeklyReportResult;
import com.xuanhuo.service.WeeklyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/weeklyReport")
public class WeeklyReportController extends BaseController {

    @Autowired
    private WeeklyReportService weeklyReportService;

    @Value("${report.serializable-file}")
    private String serializableFile;

    @GetMapping(value = "/nacostest/{id}")
    public String getPayment(@PathVariable("id") Integer id)
    {
        return "nacos registry, serverPort: "+"\t id"+id;
    }

    private  StaticDate staticDate;


    /**
     * 周报统计，逻辑：
     * 报表中的各个段落中的表格或图表中的数据来源于DFZ\GDATA3\HIVE\YMFXYP等四个数据源，每个数据源中可统计的内容配置在ReportConstants常量类中的
     * SDFZ_CONFIG、GDATA3_CONFIG、YMFXYP_CONFIG、HIVE_CONFIG等四个map中，然后调研sdfz数据源中的f_tj_sql函数将统计开始、结束日期及以上map中的
     * 配置信息传递给它，最终会得到对应配置项的统计SQL，然后执行SQL等到配置项的具体数值。
     *
     * 1、先根据相关配置ReportConstants及f_tj_sql函数，获取到待在相关数据源中执行的SQL。
     * 2、分别在SDFZ\GDATA3\HIVE\YMFXYP等数据源中执行相关SQL，并获取结果集
     * 3、将结果集转化成python需要的json字段及格式，包含上周和本周数据
     * 4、上周数据序列化成文件存储在resources\seiralizableFile下，取的时候用反序列化取出
     *
     * @param date
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @GetMapping("/data")
    public AjaxResult getWeeklyReportData(String date, boolean recount) throws ExecutionException, InterruptedException,Exception {
        staticDate = new StaticDate(date);
        logger.debug("======开始统计本周数据:");
        logData(staticDate);
        //本周数据先取序列化后的，没有再重新统计
        if(!recount){
            if(StrUtil.isEmpty(date)){
                date = DateUtil.format(DateUtil.date(),"yyyyMMdd");
            }
            WeeklyReportResult thisWeeklyReport = getSerializableReport(date);
            if(thisWeeklyReport != null){
                logger.info("读取本周序列化数据：");
                return AjaxResult.success(thisWeeklyReport);
            }
        }


        IWeeklyReportResultBuilder builder = new WeeklyReportResultBuilderImpl(staticDate);
        IWeeklyReportResultDirector director = new WeeklyReportResultDirectorImpl(builder);
        director.buildWeeklyReportResult();

        //上周数据
        //统计时间
        String lastWeekDate = date;
        if(StrUtil.isEmpty(lastWeekDate)){
            lastWeekDate = DateUtil.format(DateUtil.date(),"yyyyMMdd");
        }
        lastWeekDate = DateUtil.format((DateUtil.offsetDay(DateUtil.parse(lastWeekDate,"yyyyMMdd"),-7)),"yyyyMMdd");
        WeeklyReportResult lastWeeklyReport = getSerializableReport(lastWeekDate);
        if(lastWeeklyReport != null && !recount){
            logger.info("读取上周序列化数据：");
            builder.buildeLastWeekData(lastWeeklyReport);
        }else{
            staticDate = new StaticDate(lastWeekDate);
            logger.debug("======开始统计上周数据:");
            logData(staticDate);
            IWeeklyReportResultBuilder lastWeekBuilder = new WeeklyReportResultBuilderImpl(staticDate);
            IWeeklyReportResultDirector lastWeekDirector = new WeeklyReportResultDirectorImpl(lastWeekBuilder);
            lastWeekDirector.buildWeeklyReportResult();
            WeeklyReportResult lastWeekResult = lastWeekBuilder.getWeeklyReportResult();
            logger.info("=======序列化上周数据：日期：{}",date);
            serializeResult(lastWeekResult,lastWeekDate);
            logger.info("=======结束序列化上周数据：日期：{}",date);
            builder.buildeLastWeekData(lastWeekResult);
        }

        WeeklyReportResult weeklyReportResult = builder.getWeeklyReportResult();
        logger.info("=======序列化本周数据：日期：{}",date);
        serializeResult(weeklyReportResult,date);
        logger.info("=======结束序列化本周数据：日期：{}",date);
        return AjaxResult.success(weeklyReportResult);
    }

    @Scheduled(cron = "0 0 20 * * *")
    public AjaxResult getWeeklyReportData() throws ExecutionException, InterruptedException,Exception {
        return this.getWeeklyReportData(DateUtil.format(new Date(),"yyyyMMdd"),true);
    }

    private void logData(StaticDate staticDate){
        logger.debug("======报表日期：{}月{}日",staticDate.getMonth(),staticDate.getDay());
        logger.debug("======WARN统计日期为：{}至{}",staticDate.getWarnStartDate(),staticDate.getWarnEndDate());
        logger.debug("======WEBSITE统计日期为：{}至{}",staticDate.getWebsitStartDate(),staticDate.getWebsitEndDate());
        logger.debug("======week1：{}至{}",staticDate.getWeek1Start(),staticDate.getWeek1End());
        logger.debug("======week2：{}至{}",staticDate.getWeek2Start(),staticDate.getWeek2End());
        logger.debug("======week3：{}至{}",staticDate.getWeek3Start(),staticDate.getWeek3End());
        logger.debug("======week4：{}至{}",staticDate.getWeek4Start(),staticDate.getWeek4End());
        logger.debug("======dateList:{}",staticDate.getDateList().toString());
    }

    private void  serializeResult(WeeklyReportResult weeklyReportResult,String date){
        //序列化weeklyReportResult
        String serDate = null;
        if(StrUtil.isEmpty(date)){
            serDate = DateUtil.format(DateUtil.date(),"yyyyMMdd");
        }else{
            serDate = DateUtil.format((DateUtil.parse(date,"yyyyMMdd")),"yyyyMMdd");
        }
        SerializableUtil<WeeklyReportResult> serializableUtil = new SerializableUtil<>();
        serializableUtil.serializableObjectToFile(weeklyReportResult,serializableFile+"." + serDate);
    }

    /**
     * 反序列化上周数据
     * @return
     */
    private WeeklyReportResult getSerializableReport(String date){
        //1、初始化相关类
        SerializableUtil<WeeklyReportResult> serializableUtil = new SerializableUtil<>();
        WeeklyReportResult weeklyReportResult = serializableUtil.deSerializableObjectFromFile(serializableFile+"."+date, new Class[]{WeeklyReportResult.class,List.class,byte[].class}, WeeklyReportResult.class);
        return weeklyReportResult;
    }




}

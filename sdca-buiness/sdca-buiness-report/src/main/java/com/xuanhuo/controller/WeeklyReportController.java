package com.xuanhuo.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.xuanhuo.common.core.constant.ReportConstants;
import com.xuanhuo.common.core.controller.BaseController;
import com.xuanhuo.common.core.domain.AjaxResult;
import com.xuanhuo.common.core.utils.MapUtil;
import com.xuanhuo.pojo.StaticDate;
import com.xuanhuo.pojo.WeeklyReportResult;
import com.xuanhuo.service.TjBbPzService;
import com.xuanhuo.service.WeeklyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sun.awt.image.ImageWatched;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/weeklyReport")
public class WeeklyReportController extends BaseController {

    @Autowired
    private WeeklyReportService weeklyReportService;

    @GetMapping("/data")
    public AjaxResult getWeeklyReportData(String date){
        //1、初始化相关类
        //统计时间
        StaticDate staticDate = new StaticDate(date);
        //初始化与python接口数据对象
        WeeklyReportResult weeklyReportResult = new WeeklyReportResult(staticDate);

        //2、统计数据
        logger.debug("报表日期：{}月{}日",staticDate.getMonth(),staticDate.getDay());
        logger.debug("WARN统计日期为：{}至{}",staticDate.getWarnStartDate(),staticDate.getWarnEndDate());
        logger.debug("WEBSITE统计日期为：{}至{}",staticDate.getWebsitStartDate(),staticDate.getWebsitEndDate());
        //SDFZ数据源  数据
        Map<String, Object> sdfzResult = getSDFZResult(staticDate.getWarnStartDate(), staticDate.getWarnEndDate());
        //GDATA3数据源 数据
        Map<String, Object> gdata3Result = getGDATA3Result(staticDate.getWebsitStartDate(), staticDate.getWebsitEndDate());

        //3、拼装python接口对象
        dellSDFZData(sdfzResult,weeklyReportResult.getTable());
        dellGDATA3Data(gdata3Result,weeklyReportResult.getTable());

        logger.debug("sdfz数据：{}",sdfzResult);
        logger.debug("gdata3数据：{}",gdata3Result);

        return AjaxResult.success(weeklyReportResult);
    }

    /**
     * SDFZ数据源相关数据
     * @param ksrq
     * @param jsrq
     * @return
     */
    public Map<String,Object> getSDFZResult(String ksrq,String jsrq){
        logger.debug("======开始执行f_tj_sql函数并获取SDFZ相关SQL");
        Map<String,Object> sdfzResult = new HashMap<>();
        //根据ReportConstants.SDFZ_CONFIG的配制文件，调用f_tj_sql函数获取SQL并执行
        for(Map.Entry<String, String> entry : ReportConstants.SDFZ_CONFIG.entrySet()){
            Map<String,String> param = new HashMap<>();
            param.put("bbbm",ReportConstants.ALIZB_CODE);
            param.put("zbbm",entry.getKey());
            param.put("ksrq",ksrq);
            param.put("jsrq",jsrq);
            weeklyReportService.callSqlFunction(param);
            logger.debug("======bbbm:{},zbbm:{},ksrq:{},jsrq:{}。获取的SQL:{}",param.get("bbbm"),param.get("zbbm"),param.get("ksrq"),param.get("jsrq"),param.get("sql"));
            List<Map<String, Object>> sdfzSqlResult = weeklyReportService.getSDFZSqlResult(param.get("sql"));
            sdfzResult.put(entry.getValue(),sdfzSqlResult);
        }
        return sdfzResult;
    }

    /**
     * GDATA数据源相关数据
     * @param ksrq
     * @param jsrq
     * @return
     */
    public Map<String,Object> getGDATA3Result(String ksrq,String jsrq){
        logger.debug("======开始执行f_tj_sql函数并获取GDATA3相关SQL");
        Map<String,Object> gData3Result = new HashMap<>();
        //根据ReportConstants.GDATA3_CONFIG的配制文件，调用f_tj_sql函数获取SQL并执行
        for(Map.Entry<String, String> entry : ReportConstants.GDATA3_CONFIG.entrySet()){
            Map<String,String> param = new HashMap<>();
            param.put("bbbm",ReportConstants.ALIZB_CODE);
            param.put("zbbm",entry.getKey());
            param.put("ksrq",ksrq);
            param.put("jsrq",jsrq);
            weeklyReportService.callSqlFunction(param);
            logger.debug("======bbbm:{},zbbm:{},ksrq:{},jsrq:{}。获取的SQL:{}",param.get("bbbm"),param.get("zbbm"),param.get("ksrq"),param.get("jsrq"),param.get("sql"));
            List<Map<String, Object>> sdfzSqlResult = weeklyReportService.getGdata3SqlResult(param.get("sql"));
            gData3Result.put(entry.getValue(),sdfzSqlResult);
        }
        return gData3Result;
    }

    /**
     * 处理SDFZ数据源的数据
     * @param sdfzResult
     * @param table
     */
    private void dellSDFZData(Map<String, Object> sdfzResult,List<Map<String,Object>> table){
        //贷款、代办信用卡类
        Map<String,Object> dk_dbxykl = new LinkedHashMap<>();
        dk_dbxykl.put("name","贷款、代办信用卡类");
        dk_dbxykl.put("website",null);
        dk_dbxykl.put("warning",null);
        dk_dbxykl.put("website_last_week",null);
        dk_dbxykl.put("warning_last_week",null);

        Map<String,Object> data = (LinkedHashMap)((ArrayList) sdfzResult.get("贷款、代办信用卡类预警统计")).get(0);
        Map<String,Object> newData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(data,newData);
        dk_dbxykl.put("warning",newData);

        table.add(dk_dbxykl);

        //刷单返利类
        Map<String,Object> sdfll = new LinkedHashMap<>();
        sdfll.put("name","刷单返利类");
        sdfll.put("website",null);
        sdfll.put("warning",null);
        sdfll.put("website_last_week",null);
        sdfll.put("warning_last_week",null);

        Map<String,Object> sdflOldData = (LinkedHashMap)((ArrayList) sdfzResult.get("刷单返利类预警统计")).get(0);
        Map<String,Object> sdflNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(sdflOldData,sdflNewData);
        sdfll.put("warning",sdflNewData);

        table.add(sdfll);

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

        Map<String,Object> szplOldData = (LinkedHashMap)((ArrayList) sdfzResult.get("杀猪盘类预警统计")).get(0);
        Map<String,Object> szplNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(szplOldData,szplNewData);
        szpl.put("warning",szplNewData);

        Map<String,Object> szplyjfltj = (LinkedHashMap)((ArrayList) sdfzResult.get("杀猪盘类预警分类统计")).get(0);
        szpl.put("warning_category",szplyjfltj);
        table.add(szpl);

        //虚假ETC专项
        Map<String,Object> xjETC = new LinkedHashMap<>();
        xjETC.put("name","虚假ETC专项");
        xjETC.put("website",null);
        xjETC.put("warning",null);
        xjETC.put("message",null);
        xjETC.put("website_last_week",null);
        xjETC.put("warning_last_week",null);
        xjETC.put("message",null);

        Map<String,Object> xjETCOldData = (LinkedHashMap)((ArrayList) sdfzResult.get("虚假ETC预警统计")).get(0);
        Map<String,Object> sxjETCNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(xjETCOldData,sxjETCNewData);
        xjETC.put("warning",sxjETCNewData);

        table.add(xjETC);

        //公安提供的其他类型诈骗网站及预警
        Map<String,Object> gaqtl = new LinkedHashMap<>();
        gaqtl.put("name","公安提供的其他类型诈骗网站及预警");
        gaqtl.put("website",null);
        gaqtl.put("warning",null);
        gaqtl.put("message",null);
        gaqtl.put("website_last_week",null);
        gaqtl.put("warning_last_week",null);
        gaqtl.put("message_last_week",null);

        Map<String,Object> gaqtlOldData = (LinkedHashMap)((ArrayList) sdfzResult.get("公安其他类型预警统计")).get(0);
        Map<String,Object> gaqtlNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(gaqtlOldData,gaqtlNewData);
        gaqtl.put("warning",gaqtlNewData);

        table.add(gaqtl);

        //各类型预警按地市统计
        Map<String,Object> glxyjadstj = new LinkedHashMap<>();
        glxyjadstj.put("name","各类型预警按地市统计");
        glxyjadstj.put("last_week_warning",null);
        glxyjadstj.put("city",null);


        List<Map<String,Object>> glxyjadstjOldData = (ArrayList<Map<String,Object>>) sdfzResult.get("地市预警分类排序");
        List glxyjadstjNewData = new ArrayList();
        glxyjadstjOldData.stream().forEach(eachData -> {
            eachData.remove("序号");
            Map newEachData = new LinkedHashMap();
            newEachData.put("city_name",eachData.get("地市"));
            eachData.remove("地市");
            MapUtil.exchangeReportDate(eachData,newEachData);
            glxyjadstjNewData.add(newEachData);
        });
        glxyjadstj.put("city",glxyjadstjNewData);

        table.add(glxyjadstj);
    }

    /**
     * 处理GDATA3数据源的数据
     * @param gdata3Result
     * @param table
     */
    private void dellGDATA3Data(Map<String, Object> gdata3Result,List<Map<String,Object>> table){
        //贷款、代办信用卡类
        Map<String, Object> dk_dbxykl = table.get(0);

        Map<String,Object> oldData = (LinkedHashMap)((ArrayList) gdata3Result.get("贷款、代办信用卡类网站统计")).get(0);
        Map<String,Object> newData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(oldData,newData);
        dk_dbxykl.put("website",newData);

        //刷单返利类
        Map<String, Object> sdfll = table.get(1);
        Map<String,Object> sdflOldData = (LinkedHashMap)((ArrayList) gdata3Result.get("刷单返利类网站统计")).get(0);
        Map<String,Object> sdflNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(sdflOldData,sdflNewData);
        sdfll.put("website",sdflNewData);

        //杀猪盘类
        Map<String, Object> szpl = table.get(2);
        Map<String,Object> szplOldData = (LinkedHashMap)((ArrayList) gdata3Result.get("杀猪盘类网站统计")).get(0);
        Map<String,Object> szplNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(szplOldData,szplNewData);
        szpl.put("website",szplNewData);

        //这一块不建议进行data1,data2。。。。这样的转换，因为名称可以会变
        Map<String,Object> szpwzfltj = (LinkedHashMap)((ArrayList) gdata3Result.get("杀猪盘类网站分类统计")).get(0);
        szpl.put("website_category",szplNewData);

        //虚假ETC专项
        Map<String, Object> xjETC = table.get(3);
        Map<String,Object> xjETCOldData = (LinkedHashMap)((ArrayList) gdata3Result.get("虚假ETC网站统计")).get(0);
        Map<String,Object> sxjETCNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(xjETCOldData,sxjETCNewData);
        xjETC.put("website",sxjETCNewData);


    }

}

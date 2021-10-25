package com.xuanhuo.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.xuanhuo.common.core.constant.ReportConstants;
import com.xuanhuo.common.core.controller.BaseController;
import com.xuanhuo.common.core.domain.AjaxResult;
import com.xuanhuo.common.core.utils.MapUtil;
import com.xuanhuo.common.core.utils.SerializableUtil;
import com.xuanhuo.pojo.StaticDate;
import com.xuanhuo.pojo.WeeklyReportResult;
import com.xuanhuo.service.WeeklyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

// TODO: 整个类待优化重构，目前controller中的逻辑太多
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
    public AjaxResult getWeeklyReportData(String date) throws ExecutionException, InterruptedException {

        //1、初始化相关类
        //统计时间
        StaticDate staticDate = new StaticDate(date);
        //初始化与python接口数据对象
        WeeklyReportResult weeklyReportResult = new WeeklyReportResult(staticDate);
        //上周数据
        WeeklyReportResult lastWeeklyReport = getLastWeekReport();
        if(lastWeeklyReport != null){
            dellLastWeekData(weeklyReportResult,lastWeeklyReport);
        }else{
            WeeklyReportResult lastWeeklyReportData = getLastWeeklyReportData(date);
            dellLastWeekData(weeklyReportResult,lastWeeklyReportData);
        }
        //2、统计数据
        logger.debug("======开始统计本周数据:");
        logger.debug("======报表日期：{}月{}日",staticDate.getMonth(),staticDate.getDay());
        logger.debug("======WARN统计日期为：{}至{}",staticDate.getWarnStartDate(),staticDate.getWarnEndDate());
        logger.debug("======WEBSITE统计日期为：{}至{}",staticDate.getWebsitStartDate(),staticDate.getWebsitEndDate());
        logger.debug("======week1：{}至{}",staticDate.getWeek1Start(),staticDate.getWeek1End());
        logger.debug("======week2：{}至{}",staticDate.getWeek2Start(),staticDate.getWeek2End());
        logger.debug("======week3：{}至{}",staticDate.getWeek3Start(),staticDate.getWeek3End());
        logger.debug("======week4：{}至{}",staticDate.getWeek4Start(),staticDate.getWeek4End());
        //SDFZ数据源  数据
        Map<String, Object> sdfzResult = getSDFZResult(staticDate.getWarnStartDate(), staticDate.getWarnEndDate());
        sdfzResult.putAll(getSDFZLogData(staticDate.getWarnStartDate(), staticDate.getWarnEndDate()));
        sdfzResult.putAll(select4weekwarn(staticDate));
        sdfzResult.putAll(select4weekwarnTrend(staticDate));

        //GDATA3数据源 数据
        Map<String, Object> gdata3Result = getGDATA3Result(staticDate.getWebsitStartDate(), staticDate.getWebsitEndDate());
        //HIVE数据源 数据
        Map<String, Object> getHiveResult = getHiveResult(staticDate.getWarnStartDate(), staticDate.getWarnEndDate());
        getHiveResult.putAll(getHybbzzl(staticDate.getWarnStartDate(), staticDate.getWarnEndDate()));
        getHiveResult.putAll(getHybkljzl());
        getHiveResult.putAll(getHybkFourWeek(staticDate.getWeek1Start(), staticDate.getWeek4End()));
        getHiveResult.putAll(getLogQuality(staticDate.getLogQuaDate()));

        //YMFXYP数据源 数据
        Map<String, Object> getYMFXYPResult = getYMFXYPResult(staticDate.getWarnStartDate(), staticDate.getWarnEndDate());

        //3、拼装python接口对象
        dellSDFZData(sdfzResult,weeklyReportResult);
        dellGDATA3Data(gdata3Result,weeklyReportResult.getTable());
        dellGHData(getHiveResult,weeklyReportResult);
        dellYmfxypData(getYMFXYPResult,weeklyReportResult.getTable());

        logger.debug("======本周sdfz数据：{}",sdfzResult);
        logger.debug("======本周gdata3数据：{}",gdata3Result);
        logger.debug("======本周hive1.1.0数据：{}",getHiveResult);
        logger.debug("======本周ymfxyp数据：{}",getYMFXYPResult);
        logger.debug("======本周数据统计完成！！！");

        //序列化weeklyReportResult
        SerializableUtil<WeeklyReportResult> serializableUtil = new SerializableUtil<>();
        serializableUtil.serializableObjectToFile(weeklyReportResult,serializableFile+"." + DateUtil.format(new Date(),"yyyyMMdd"));
        return AjaxResult.success(weeklyReportResult);
    }

    /**
     * 统计上周数据
     * @return
     */
    private WeeklyReportResult getLastWeeklyReportData(String date) throws ExecutionException, InterruptedException {
        //1、初始化相关类
        //统计时间
        if(StrUtil.isEmpty(date)){
            date = DateUtil.format(DateUtil.date(),"yyyyMMdd");
        }
        date = DateUtil.format((DateUtil.offsetDay(DateUtil.parse(date,"yyyyMMdd"),-7)),"yyyyMMdd");
        StaticDate staticDate = new StaticDate(date);
        //初始化与python接口数据对象
        WeeklyReportResult weeklyReportResult = new WeeklyReportResult(staticDate);

        //2、统计数据
        logger.debug("======开始统计上周数据:");
        logger.debug("======报表日期：{}月{}日",staticDate.getMonth(),staticDate.getDay());
        logger.debug("======WARN统计日期为：{}至{}",staticDate.getWarnStartDate(),staticDate.getWarnEndDate());
        logger.debug("======WEBSITE统计日期为：{}至{}",staticDate.getWebsitStartDate(),staticDate.getWebsitEndDate());
        //SDFZ数据源  数据
        Map<String, Object> sdfzResult = getSDFZResult(staticDate.getWarnStartDate(), staticDate.getWarnEndDate());
        sdfzResult.putAll(getSDFZLogData(staticDate.getWarnStartDate(), staticDate.getWarnEndDate()));
        sdfzResult.putAll(select4weekwarn(staticDate));
        sdfzResult.putAll(select4weekwarnTrend(staticDate));

        //GDATA3数据源 数据
        Map<String, Object> gdata3Result = getGDATA3Result(staticDate.getWebsitStartDate(), staticDate.getWebsitEndDate());
        //HIVE数据源 数据
        Map<String, Object> getHiveResult = getHiveResult(staticDate.getWebsitStartDate(), staticDate.getWebsitEndDate());
        getHiveResult.putAll(getHybbzzl(staticDate.getWarnStartDate(), staticDate.getWarnEndDate()));
        getHiveResult.putAll(getHybkljzl());
        getHiveResult.putAll(getHybkFourWeek(staticDate.getWarnStartDate(), staticDate.getWarnEndDate()));
        //YMFXYP数据源 数据
        Map<String, Object> getYMFXYPResult = getYMFXYPResult(staticDate.getWebsitStartDate(), staticDate.getWebsitEndDate());

        //3、拼装python接口对象
        dellSDFZData(sdfzResult,weeklyReportResult);
        dellGDATA3Data(gdata3Result,weeklyReportResult.getTable());
        dellGHData(getHiveResult,weeklyReportResult);
        dellYmfxypData(getYMFXYPResult,weeklyReportResult.getTable());

        logger.debug("======上周sdfz数据：{}",sdfzResult);
        logger.debug("======上周gdata3数据：{}",gdata3Result);
        logger.debug("======上周hive1.1.0数据：{}",getHiveResult);
        logger.debug("======上周ymfxyp数据：{}",getYMFXYPResult);
        logger.debug("======上周数据统计完成！！！");

        //序列化weeklyReportResult
        SerializableUtil<WeeklyReportResult> serializableUtil = new SerializableUtil<>();
        serializableUtil.serializableObjectToFile(weeklyReportResult,serializableFile+"." + date);
        return weeklyReportResult;
    }

    /**
     * SDFZ数据源相关数据
     * @param ksrq
     * @param jsrq
     * @return
     */
    public Map<String,Object> getSDFZResult(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        logger.debug("======开始执行f_tj_sql函数并获取SDFZ相关SQL");
        Map<String,Object> sdfzResult = new HashMap<>();
        Map<String,Future<List<Map<String, Object>>>> futureMap = new HashMap<>();
        //根据ReportConstants.SDFZ_CONFIG的配制文件，调用f_tj_sql函数获取SQL并执行
        for(Map.Entry<String, String> entry : ReportConstants.SDFZ_CONFIG.entrySet()){
            Map<String,String> param = new HashMap<>();
            param.put("bbbm",ReportConstants.ALIZB_CODE);
            param.put("zbbm",entry.getKey());
            param.put("ksrq",ksrq);
            param.put("jsrq",jsrq);
            weeklyReportService.callSqlFunction(param);
            logger.debug("======bbbm:{},zbbm:{},ksrq:{},jsrq:{}。获取的SQL:{}--{}",param.get("bbbm"),param.get("zbbm"),param.get("ksrq"),param.get("jsrq"),entry.getValue(),param.get("sql"));
            Future<List<Map<String, Object>>> sdfzSqlResult = weeklyReportService.getSDFZSqlResult(param.get("sql"));
            futureMap.put(entry.getValue(),sdfzSqlResult);
        }
        for(Map.Entry<String, Future<List<Map<String, Object>>>> entry : futureMap.entrySet()){
            sdfzResult.put(entry.getKey(),entry.getValue().get());
        }

        return sdfzResult;
    }

    /**
     * log日志统计
     * @param ksrq
     * @param jsrq
     * @return
     */
    public Map<String,Object> getSDFZLogData(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        Map<String,Object> sdfzResult = new HashMap<>();
        Future<List<Map<String, Object>>> sdfzLogData = weeklyReportService.getSDFZLogData(ksrq, jsrq);
        sdfzResult.put("互联网日志接入量统计",sdfzLogData.get());
        return sdfzResult;
    }

    /**
     * log日志统计
     * @return
     */
    public Map<String,Object> getLogQuality(String rq) throws ExecutionException, InterruptedException {

        Map<String,Object> logQuaRes = new HashMap<>();
        Future<List<Map<String, String>>> hiveLogData = weeklyReportService.getLogQuality(rq);
        logQuaRes.put("互联网日志质量统计",hiveLogData.get());
        logger.debug("互联网日志质量统计{}",hiveLogData.get());
        return logQuaRes;
    }

    /**
     * 预警数据近一周的活跃网站数量统计
     * @param staticDate
     * @return
     */
    public Map<String,Object> select4weekwarn(StaticDate staticDate) throws ExecutionException, InterruptedException {
        Map<String,Object> sdfzResult = new HashMap<>();
        Future<List<Map<String, String>>> fourWeekWarn = weeklyReportService.select4weekwarn(staticDate);
        sdfzResult.put("预警数据近一周的活跃网站数量统计",fourWeekWarn.get());
        return sdfzResult;
    }

    /**
     * 预警数据近一周的活跃网站数量统计
     * @param staticDate
     * @return
     */
    public Map<String,Object> select4weekwarnTrend(StaticDate staticDate) throws ExecutionException, InterruptedException {
        Map<String,Object> sdfzResult = new HashMap<>();
        Future<List<Map<String, String>>> fourWeekWarn = weeklyReportService.select4weekwarnTrend(staticDate);
        sdfzResult.put("预警数据近四周变化趋势统计",fourWeekWarn.get());
        return sdfzResult;
    }

    /**
     * GDATA数据源相关数据
     * @param ksrq
     * @param jsrq
     * @return
     */
    public Map<String,Object> getGDATA3Result(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        logger.debug("======开始执行f_tj_sql函数并获取GDATA3相关SQL");
        Map<String,Object> gData3Result = new HashMap<>();
        Map<String,Future<List<Map<String, Object>>>> futureMap = new HashMap<>();
        //根据ReportConstants.GDATA3_CONFIG的配制文件，调用f_tj_sql函数获取SQL并执行
        for(Map.Entry<String, String> entry : ReportConstants.GDATA3_CONFIG.entrySet()){
            Map<String,String> param = new HashMap<>();
            param.put("bbbm",ReportConstants.ALIZB_CODE);
            param.put("zbbm",entry.getKey());
            param.put("ksrq",ksrq);
            param.put("jsrq",jsrq);
            weeklyReportService.callSqlFunction(param);
            logger.debug("======bbbm:{},zbbm:{},ksrq:{},jsrq:{}。获取的SQL:{}",param.get("bbbm"),param.get("zbbm"),param.get("ksrq"),param.get("jsrq"),param.get("sql"));
            Future<List<Map<String, Object>>> gdata3SqlResult = weeklyReportService.getGdata3SqlResult(param.get("sql"));
            futureMap.put(entry.getValue(),gdata3SqlResult);

        }
        for(Map.Entry<String, Future<List<Map<String, Object>>>> entry : futureMap.entrySet()){
            gData3Result.put(entry.getKey(),entry.getValue().get());
        }
        return gData3Result;
    }

    /**
     * Hive数据源相关数据
     * @param ksrq
     * @param jsrq
     * @return
     */
    public Map<String,Object> getHiveResult(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        logger.debug("======开始执行f_tj_sql函数并获取HIVE相关SQL");
        Map<String,Object> hiveResult = new HashMap<>();
        Map<String,Future<List<Map<String, Object>>>> futureMap = new HashMap<>();
        //根据ReportConstants.HIVE_CONFIG的配制文件，调用f_tj_sql函数获取SQL并执行
        for(Map.Entry<String, String> entry : ReportConstants.HIVE_CONFIG.entrySet()){
            Map<String,String> param = new HashMap<>();
            param.put("bbbm",ReportConstants.ALIZB_CODE);
            param.put("zbbm",entry.getKey());
            param.put("ksrq",ksrq);
            param.put("jsrq",jsrq);
            weeklyReportService.callSqlFunction(param);
            logger.debug("======bbbm:{},zbbm:{},ksrq:{},jsrq:{}。获取的SQL:{}",param.get("bbbm"),param.get("zbbm"),param.get("ksrq"),param.get("jsrq"),param.get("sql"));
            Future<List<Map<String, Object>>> hiveSqlResult = weeklyReportService.getHiveSqlResult(param.get("sql"));
            futureMap.put(entry.getValue(),hiveSqlResult);

        }

        for(Map.Entry<String, Future<List<Map<String, Object>>>> entry : futureMap.entrySet()){
            hiveResult.put(entry.getKey(),entry.getValue().get());
        }
        return hiveResult;
    }

    /**
     * 黑样本本周总量
     * @return
     */
    public Map<String,Object> getHybbzzl(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        Map<String,Object> hiveResult = new HashMap<>();
        Future<Map<String, String>> hybbzzl = weeklyReportService.getHybbzzl(ksrq,jsrq);
        hiveResult.put("黑样本本周总量",hybbzzl.get());
        return hiveResult;
    }

    /**
     * 黑样本累计总量
     * @return
     */
    public Map<String,Object> getHybkljzl() throws ExecutionException, InterruptedException {
        Map<String,Object> hiveResult = new HashMap<>();
        Future<Map<String, String>> hybbzzl = weeklyReportService.getHybkljzl();
        hiveResult.put("黑样本累计总量",hybbzzl.get());
        return hiveResult;
    }

    /**
     * 黑样本库近四周数据
     * @return
     */
    public Map<String,Object> getHybkFourWeek(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        Map<String,Object> hiveResult = new HashMap<>();
        Future<List<Map<String, String>>> hybkFourWeek = weeklyReportService.getHybkFourWeek(ksrq,jsrq);
        hiveResult.put("黑样本库近四周数据",hybkFourWeek.get());
        return hiveResult;
    }

    /**
     * GDATA数据源相关数据
     * @param ksrq
     * @param jsrq
     * @return
     */
    public Map<String,Object> getYMFXYPResult(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        logger.debug("======开始执行f_tj_sql函数并获取YMFXYP相关SQL");
        Map<String,Object> ymfxypResult = new HashMap<>();
        Map<String,Future<List<Map<String, Object>>>> futureMap = new HashMap<>();
        //根据ReportConstants.YMFXYP_CONFIG，调用f_tj_sql函数获取SQL并执行
        for(Map.Entry<String, String> entry : ReportConstants.YMFXYP_CONFIG.entrySet()){
            Map<String,String> param = new HashMap<>();
            param.put("bbbm",ReportConstants.ALIZB_CODE);
            param.put("zbbm",entry.getKey());
            param.put("ksrq",ksrq);
            param.put("jsrq",jsrq);
            weeklyReportService.callSqlFunction(param);
            logger.debug("======bbbm:{},zbbm:{},ksrq:{},jsrq:{}。获取的SQL:{}",param.get("bbbm"),param.get("zbbm"),param.get("ksrq"),param.get("jsrq"),param.get("sql"));
            Future<List<Map<String, Object>>> ymfxypSqlResult = weeklyReportService.getYMFXYPSqlResult(param.get("sql"));
            futureMap.put(entry.getValue(),ymfxypSqlResult);

        }
        for(Map.Entry<String, Future<List<Map<String, Object>>>> entry : futureMap.entrySet()){
            ymfxypResult.put(entry.getKey(),entry.getValue().get());
        }
        return ymfxypResult;
    }

    /**
     * 处理SDFZ数据源的数据
     * @param sdfzResult
     * @param weeklyReportResult
     */
    private void dellSDFZData(Map<String, Object> sdfzResult,WeeklyReportResult weeklyReportResult){
        List<Map<String, Object>> table = weeklyReportResult.getTable();
        //贷款、代办信用卡类
        Map<String, Object> dk_dbxykl = table.get(0);
        Map<String,Object> data = (LinkedHashMap)((ArrayList) sdfzResult.get("贷款、代办信用卡类预警统计")).get(0);
        Map<String,Object> newData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(data,newData);
        dk_dbxykl.put("warning",newData);


        //刷单返利类
        Map<String, Object> sdfll = table.get(1);
        Map<String,Object> sdflOldData = (LinkedHashMap)((ArrayList) sdfzResult.get("刷单返利类预警统计")).get(0);
        Map<String,Object> sdflNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(sdflOldData,sdflNewData);
        sdfll.put("warning",sdflNewData);

        //杀猪盘类
        Map<String, Object> szpl = table.get(2);
        Map<String,Object> szplOldData = (LinkedHashMap)((ArrayList) sdfzResult.get("杀猪盘类预警统计")).get(0);
        Map<String,Object> szplNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(szplOldData,szplNewData);
        szpl.put("warning",szplNewData);
        //处理杀猪盘类预警分类统计，----列名是固定的
        List<Map<String,Object>> szplyjfltjList = (ArrayList<Map<String,Object>>) sdfzResult.get("杀猪盘类预警分类统计");
        Map<String,Object> szplyjfltj = new LinkedHashMap<>();
        szplyjfltj.put("data1",0);
        szplyjfltj.put("data2",0);
        szplyjfltj.put("data3",0);
        szplyjfltjList.stream().forEach(eachMap -> {
            if(StrUtil.equals((String)eachMap.get("modeltype"),"杀猪盘类-虚假投资理财")){
                szplyjfltj.put("data1",eachMap.get("sl"));
            }else if(StrUtil.equals((String)eachMap.get("modeltype"),"杀猪盘类-虚假博彩")){
                szplyjfltj.put("data2",eachMap.get("sl"));
            }else{
                szplyjfltj.put("data3",eachMap.get("sl"));
            }
        });
        szpl.put("warning_category",szplyjfltj);

        //虚假ETC专项
        Map<String, Object> xjETC = table.get(3);
        Map<String,Object> xjETCOldData = (LinkedHashMap)((ArrayList) sdfzResult.get("虚假ETC预警统计")).get(0);
        Map<String,Object> sxjETCNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(xjETCOldData,sxjETCNewData);
        xjETC.put("warning",sxjETCNewData);

        //公安提供的其他类型诈骗网站及预警
        Map<String, Object> gaqtl = table.get(4);
        Map<String,Object> gaqtlOldData = (LinkedHashMap)((ArrayList) sdfzResult.get("公安其他类型预警统计")).get(0);
        Map<String,Object> gaqtlNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(gaqtlOldData,gaqtlNewData);
        gaqtl.put("warning",gaqtlNewData);

        //各类型预警按地市统计
        Map<String, Object> glxyjadstj = table.get(5);
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

        //互联网日志
        List<Map<String,Object>> hlyrzjrltj = (ArrayList<Map<String,Object>>) sdfzResult.get("互联网日志接入量统计");
        Map<String, String> lt = weeklyReportResult.getLt();
        Map<String, String> yd = weeklyReportResult.getYd();
        Map<String, String> dx = weeklyReportResult.getDx();

        int ltIndex = 1;
        int ydIndex = 1;
        int dxIndex = 1;
        for(Map<String,Object> map : hlyrzjrltj ){
            if(StrUtil.equals(String.valueOf(map.get("type")),"山东电信")){
                dx.put("data" + dxIndex++,String.valueOf(map.get("nu")));
            }
            if(StrUtil.equals(String.valueOf(map.get("type")),"山东移动")){
                yd.put("data" + ydIndex++,String.valueOf(map.get("nu")));
            }

            if(StrUtil.equals(String.valueOf(map.get("type")),"山东联通")){
                lt.put("data" + ltIndex++,String.valueOf(map.get("nu")));
            }
        }

        //预警数据近一周的活跃网站数量统计
        List<Map<String,Object>> fourWeekWarn = (ArrayList<Map<String,Object>>) sdfzResult.get("预警数据近一周的活跃网站数量统计");
        List<Map<String, Object>> table_week = weeklyReportResult.getTable_week();
        Map<String, Object> yjsjjszbhqstj = table_week.get(2);
        int yjsjjszbhqstj_Index = 1;
        for(Map<String,Object> map : fourWeekWarn){
            yjsjjszbhqstj.put("week" + yjsjjszbhqstj_Index++,map.get("nu"));
        }

        //预警数据近一周的活跃网站数量统计
        List<Map<String,Object>> fourWeekWarnTrend = (ArrayList<Map<String,Object>>) sdfzResult.get("预警数据近四周变化趋势统计");
        Map<String, Object> yjsjjszbhqstj01 = table_week.get(0);
        int yjsjjszbhqstj01_Index = 1;
        for(Map<String,Object> map : fourWeekWarnTrend){
            yjsjjszbhqstj01.put("week" + yjsjjszbhqstj01_Index++,map.get("nu"));
        }

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

        //处理杀猪盘类预警分类统计，----列名是固定的
        List<Map<String,Object>> szpwzfltjList = (ArrayList<Map<String,Object>>) gdata3Result.get("杀猪盘类网站分类统计");
        Map<String,Object> szpwzfltj = new LinkedHashMap<>();
        szpwzfltj.put("data1",0);
        szpwzfltj.put("data2",0);
        szpwzfltj.put("data3",0);
        szpwzfltjList.stream().forEach(eachMap -> {
            if(StrUtil.equals((String)eachMap.get("type"),"杀猪盘类-虚假投资理财")){
                szpwzfltj.put("data1",eachMap.get("sl"));
            }else if(StrUtil.equals((String)eachMap.get("type"),"杀猪盘类-虚假博彩")){
                szpwzfltj.put("data2",eachMap.get("sl"));
            }else{
                szpwzfltj.put("data3",eachMap.get("sl"));
            }
        });
        szpl.put("website_category",szpwzfltj);


        //虚假ETC专项
        Map<String, Object> xjETC = table.get(3);
        Map<String,Object> xjETCOldData = (LinkedHashMap)((ArrayList) gdata3Result.get("虚假ETC网站统计")).get(0);
        Map<String,Object> sxjETCNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(xjETCOldData,sxjETCNewData);
        xjETC.put("website",sxjETCNewData);


    }

    /**
     * 处理Hive1.1.0数据源的数据
     * @param gdata3Result
     */
    private void dellGHData(Map<String, Object> gdata3Result,WeeklyReportResult weeklyReportResult){
        List<Map<String, Object>> table = weeklyReportResult.getTable();
        //虚假ETC专项
        Map<String, Object> xjETC = table.get(3);
        Map<String,Object> xjETCOldData = (LinkedHashMap)((ArrayList) gdata3Result.get("虚假ETC短信统计")).get(0);
        Map<String,Object> xjETCNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(xjETCOldData,xjETCNewData);
        xjETC.put("message",xjETCNewData);

        Map<String, String> hybzlData = (Map<String, String>)gdata3Result.get("黑样本累计总量");
        Map<String, String> hybbzzl = (Map<String, String>)gdata3Result.get("黑样本本周总量");
        List<Map<String, String>> yhbkjszsj = (List<Map<String, String>>)gdata3Result.get("黑样本库近四周数据");
        List<Map<String, Object>> table_week = weeklyReportResult.getTable_week();
        Map<String, Object> map = table_week.get(1);
        map.put("history_sum",hybzlData.get("nu"));
        map.put("week_sum",hybbzzl.get("nu"));
        int yhbkjszsj_Index = 1;
        for(Map<String, String> weekMap : yhbkjszsj){
            map.put("week" + yhbkjszsj_Index++,weekMap.get("nu"));
        }

        List<Map<String, String>> logQuality =  (List<Map<String, String>>)gdata3Result.get("互联网日志质量统计");
        if(logQuality != null){
            Map<String, Object> quality = weeklyReportResult.getQuality();
            //总量统计
            Map<String,String> totalMap = new LinkedHashMap<>();
            totalMap.put("data1","0");
            totalMap.put("data2","0");
            totalMap.put("data3","0");
            //host不为空统计
            Map<String,String> hostNotNullMap = new LinkedHashMap<>();
            hostNotNullMap.put("data1","0");
            hostNotNullMap.put("data2","0");
            hostNotNullMap.put("data3","0");
            hostNotNullMap.put("data4","0");
            //ssl_sni不为空统计
            Map<String,String> sslNotNullMap = new LinkedHashMap<>();
            sslNotNullMap.put("data1","0");
            sslNotNullMap.put("data2","0");
            sslNotNullMap.put("data3","0");
            sslNotNullMap.put("data4","0");

            double totalData1 = 0.0;
            double hostData1 = 0.0;
            double sslData1 = 0.0;
            double totalData2 = 0.0;
            double sslData3 = 0.0;


            for(Map<String,String> logData : logQuality){
                if(StrUtil.equals(logData.get("num_type"),"全量数据")){


                    if(StrUtil.equals(logData.get("tablename"),"hajx_hlw_hive.t_logs")){
                        totalData1 = Double.parseDouble(logData.get("num"))/100000000;
                        String data1Str = NumberUtil.roundStr(totalData1,2);
                        totalMap.put("data1", data1Str);
                    }else{
                        totalData2 = Double.parseDouble(logData.get("num"))/100000000;
                        String data2Str = NumberUtil.roundStr(totalData2,2);
                        totalMap.put("data2", data2Str);
                    }
                    if(totalData1 != 0){
                        totalMap.put("data3",NumberUtil.roundStr(totalData2/totalData1,2));
                    }
                    quality.put("total",totalMap);
                }

                if(StrUtil.equals(logData.get("num_type"),"host不为空")){

                    if(StrUtil.equals(logData.get("tablename"),"hajx_hlw_hive.t_logs")){
                        hostData1 = Double.parseDouble(logData.get("num"))/100000000;
                        String data1 = NumberUtil.roundStr(hostData1,2);
                        hostNotNullMap.put("data1", data1);
                    }
                    quality.put("host_not_null",hostNotNullMap);
                }

                if(StrUtil.equals(logData.get("num_type"),"ssl_sni不为空")){


                    if(StrUtil.equals(logData.get("tablename"),"hajx_hlw_hive.t_logs")){
                        sslData1 = Double.parseDouble(logData.get("num"))/100000000;
                        String data1 = NumberUtil.roundStr(sslData1,2);
                        sslNotNullMap.put("data1", data1);
                    }else{
                        sslData3 =Double.parseDouble(logData.get("num"))/100000000;
                        String data3 = NumberUtil.roundStr(sslData3,2);
                        sslNotNullMap.put("data3", data3);
                    }
                    quality.put("ssl_sni_not_null",sslNotNullMap);
                }

            }

            double hostData3 = NumberUtil.round(totalData2 - sslData3,2).doubleValue();

            String hostData2 = NumberUtil.roundStr(hostData1/totalData1,2);
            String hostData4 = NumberUtil.roundStr(hostData3/totalData2,2);
            String sslData2 = NumberUtil.roundStr(sslData1/totalData1,2);
            String sslData4 = NumberUtil.roundStr(sslData3/totalData2,2);

            hostNotNullMap.put("data2",hostData2);
            hostNotNullMap.put("data3",String.valueOf(hostData3));
            hostNotNullMap.put("data4",hostData4);

            sslNotNullMap.put("data2",sslData2);
            sslNotNullMap.put("data4",sslData4);
        }



    }

    /**
     * 处理ymfxyp数据源的数据
     * @param ymfxypResult
     * @param table
     */
    private void dellYmfxypData(Map<String, Object> ymfxypResult,List<Map<String,Object>> table){
        //虚假ETC专项
        Map<String, Object> gaqtl = table.get(4);
        Map<String,Object> gaOldData = (LinkedHashMap)((ArrayList) ymfxypResult.get("公安提供网站统计")).get(0);
        Map<String,Object> gaNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(gaOldData,gaNewData);
        gaqtl.put("website",gaNewData);

    }

    /**
     * 反序列化上周数据
     * @return
     */
    private WeeklyReportResult getLastWeekReport(){
        SerializableUtil<WeeklyReportResult> serializableUtil = new SerializableUtil<>();
        WeeklyReportResult weeklyReportResult = serializableUtil.deSerializableObjectFromFile(serializableFile+"."+DateUtil.format((DateUtil.offsetDay(new Date(),-7)),"yyyyMMdd"), new Class[]{WeeklyReportResult.class,List.class,byte[].class}, WeeklyReportResult.class);
        return weeklyReportResult;
    }

    /**
     * 设置当前报表的上周数据项
     * @param current
     * @param last
     * @return
     */
    private WeeklyReportResult dellLastWeekData(WeeklyReportResult current,WeeklyReportResult last){
        List<Map<String, Object>> last_table = last.getTable();
        List<Map<String, Object>> curr_table = current.getTable();
        //贷款、代办信用卡类
        Map<String, Object> last_dkdbxykl_map = last_table.get(0);
        Map<String, Object> curr_dkdbxykl_map = curr_table.get(0);
        curr_dkdbxykl_map.put("website_last_week",last_dkdbxykl_map.get("website"));
        curr_dkdbxykl_map.put("warning_last_week",last_dkdbxykl_map.get("warning"));

        //刷单返利类
        Map<String, Object> last_sdfll_map = last_table.get(1);
        Map<String, Object> curr_sdfll_map = curr_table.get(1);
        curr_sdfll_map.put("website_last_week",last_sdfll_map.get("website"));
        curr_sdfll_map.put("warning_last_week",last_sdfll_map.get("warning"));

        //杀猪盘类
        Map<String, Object> last_szp_map = last_table.get(2);
        Map<String, Object> curr_szp_map = curr_table.get(2);
        curr_szp_map.put("website_last_week",last_szp_map.get("website"));
        curr_szp_map.put("warning_last_week",last_szp_map.get("warning"));
        curr_szp_map.put("website_category_last_week",last_szp_map.get("website_category"));
        curr_szp_map.put("warning_category_lat_week",last_szp_map.get("warning_category"));

        //虚假ETC
        Map<String, Object> last_xjetc_map = last_table.get(3);
        Map<String, Object> curr_xjetc_map = curr_table.get(3);
        curr_xjetc_map.put("website_last_week",last_xjetc_map.get("website"));
        curr_xjetc_map.put("warning_last_week",last_xjetc_map.get("warning"));
        curr_xjetc_map.put("message_last_week",last_xjetc_map.get("message"));

        //公安提供的其他类
        Map<String, Object> last_gmtgqtl_map = last_table.get(4);
        Map<String, Object> curr_gmtgqtl_map = curr_table.get(4);
        curr_gmtgqtl_map.put("website_last_week",last_gmtgqtl_map.get("website"));
        curr_gmtgqtl_map.put("warning_last_week",last_gmtgqtl_map.get("warning"));

        //各类型预警按地市统计
        List<Map<String,Object>> dsList = (ArrayList)last_table.get(5).get("city");
        Map<String, Object> curr_glxyjadstj_map = curr_table.get(5);
        dsList.stream().forEach(eachMap -> {
            if(StrUtil.equals((String)eachMap.get("city_name"),"合计")){
                curr_glxyjadstj_map.put("last_week_warning",eachMap.get("data6"));
            }
        });
        return current;
    }



}

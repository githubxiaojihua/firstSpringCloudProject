package com.xuanhuo.controller;

import cn.hutool.core.date.DateUtil;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

// TODO: 整个类待优化重构
@RestController
@RequestMapping("/weeklyReport")
public class WeeklyReportController extends BaseController {

    @Autowired
    private WeeklyReportService weeklyReportService;

    @Value("${report.serializable-file}")
    private String serializableFile;

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
            WeeklyReportResult lastWeeklyReportData = getLastWeeklyReportData();
            dellLastWeekData(weeklyReportResult,lastWeeklyReportData);
        }
        //2、统计数据
        logger.debug("======开始统计本周数据:");
        logger.debug("======报表日期：{}月{}日",staticDate.getMonth(),staticDate.getDay());
        logger.debug("======WARN统计日期为：{}至{}",staticDate.getWarnStartDate(),staticDate.getWarnEndDate());
        logger.debug("======WEBSITE统计日期为：{}至{}",staticDate.getWebsitStartDate(),staticDate.getWebsitEndDate());
        //SDFZ数据源  数据
        Map<String, Object> sdfzResult = getSDFZResult(staticDate.getWarnStartDate(), staticDate.getWarnEndDate());
        //GDATA3数据源 数据
        Map<String, Object> gdata3Result = getGDATA3Result(staticDate.getWebsitStartDate(), staticDate.getWebsitEndDate());
        //HIVE数据源 数据
        Map<String, Object> getHiveResult = getHiveResult(staticDate.getWebsitStartDate(), staticDate.getWebsitEndDate());
        //YMFXYP数据源 数据
        Map<String, Object> getYMFXYPResult = getYMFXYPResult(staticDate.getWebsitStartDate(), staticDate.getWebsitEndDate());

        //3、拼装python接口对象
        dellSDFZData(sdfzResult,weeklyReportResult.getTable());
        dellGDATA3Data(gdata3Result,weeklyReportResult.getTable());
        dellGHData(getHiveResult,weeklyReportResult.getTable());
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
    private WeeklyReportResult getLastWeeklyReportData() throws ExecutionException, InterruptedException {
        //1、初始化相关类
        //统计时间
        String date = DateUtil.format((DateUtil.offsetDay(new Date(),-7)),"yyyyMMdd");
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
        //GDATA3数据源 数据
        Map<String, Object> gdata3Result = getGDATA3Result(staticDate.getWebsitStartDate(), staticDate.getWebsitEndDate());
        //HIVE数据源 数据
        Map<String, Object> getHiveResult = getHiveResult(staticDate.getWebsitStartDate(), staticDate.getWebsitEndDate());
        //YMFXYP数据源 数据
        Map<String, Object> getYMFXYPResult = getYMFXYPResult(staticDate.getWebsitStartDate(), staticDate.getWebsitEndDate());

        //3、拼装python接口对象
        dellSDFZData(sdfzResult,weeklyReportResult.getTable());
        dellGDATA3Data(gdata3Result,weeklyReportResult.getTable());
        dellGHData(getHiveResult,weeklyReportResult.getTable());
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
     * @param table
     */
    private void dellSDFZData(Map<String, Object> sdfzResult,List<Map<String,Object>> table){
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
     * @param table
     */
    private void dellGHData(Map<String, Object> gdata3Result,List<Map<String,Object>> table){
        //虚假ETC专项
        Map<String, Object> xjETC = table.get(3);
        Map<String,Object> xjETCOldData = (LinkedHashMap)((ArrayList) gdata3Result.get("虚假ETC短信统计")).get(0);
        Map<String,Object> xjETCNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(xjETCOldData,xjETCNewData);
        xjETC.put("message",xjETCNewData);

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

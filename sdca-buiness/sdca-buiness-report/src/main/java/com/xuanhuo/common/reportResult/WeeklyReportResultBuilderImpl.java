package com.xuanhuo.common.reportResult;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.xuanhuo.common.core.constant.ReportConstants;
import com.xuanhuo.common.core.utils.MapUtil;
import com.xuanhuo.common.core.utils.SerializableUtil;
import com.xuanhuo.common.core.utils.SpringApplicationContextUtil;
import com.xuanhuo.pojo.StaticDate;
import com.xuanhuo.service.WeeklyReportOdpsService;
import com.xuanhuo.service.WeeklyReportService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Data
@NoArgsConstructor
public class WeeklyReportResultBuilderImpl implements IWeeklyReportResultBuilder{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private WeeklyReportResult result;
    private StaticDate staticDate;

    private WeeklyReportOdpsService weeklyReportOdpsService;

    @Value("${report.serializable-file}")
    private String serializableFile;

    public WeeklyReportResultBuilderImpl(StaticDate staticDate){
        this.staticDate = staticDate;
        this.weeklyReportOdpsService = SpringApplicationContextUtil.getBean(WeeklyReportOdpsService.class);
    }


    @Override
    public void initData() {
        logger.debug("======开始初始化数据============");
        result = new WeeklyReportResult();
        //初始化日期
        result.setMonth(staticDate.getMonth());
        result.setDay(staticDate.getDay());
        result.setStart_date(staticDate.getWarnStartDate());
        result.setEnd_date(staticDate.getWarnEndDate());
        result.setWeek(staticDate.getWeek());
        result.setZd(staticDate.getZd());
        //初始化qq_wx
        Map<String,String> qq = new LinkedHashMap<>();
        qq.put("data1","0");
        qq.put("data2","0");
        qq.put("data3","0");
        qq.put("data4","0");
        qq.put("data5","0");
        qq.put("data6","0");
        qq.put("data7","0");

        Map<String,String> wx = new LinkedHashMap<>();
        wx.put("data1","0");
        wx.put("data2","0");
        wx.put("data3","0");
        wx.put("data4","0");
        wx.put("data5","0");
        wx.put("data6","0");
        wx.put("data7","0");

        Map<String,Map<String,String>> qq_wx = new LinkedHashMap<>();
        qq_wx.put("qq",qq);
        qq_wx.put("wx",wx);
        result.setQq_wx(qq_wx);
        //初始化贷款、代办信用卡类相关
        Map<String,Object> dk_dbxykl = new LinkedHashMap<>();
        dk_dbxykl.put("name","贷款、代办信用卡类");
        dk_dbxykl.put("website",null);
        dk_dbxykl.put("warning",null);
        dk_dbxykl.put("website_last_week",null);
        dk_dbxykl.put("warning_last_week",null);

        //初始化刷单返利类相关
        Map<String,Object> sdfll = new LinkedHashMap<>();
        sdfll.put("name","刷单返利类");
        sdfll.put("website",null);
        sdfll.put("warning",null);
        sdfll.put("website_last_week",null);
        sdfll.put("warning_last_week",null);

        //初始化杀猪盘类相关
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

        //初始化虚假ETC专项相关
        Map<String,Object> xjETC = new LinkedHashMap<>();
        xjETC.put("name","虚假ETC专项");
        xjETC.put("website",null);
        xjETC.put("warning",null);
        xjETC.put("message",null);
        xjETC.put("website_last_week",null);
        xjETC.put("warning_last_week",null);
        xjETC.put("message",null);

        //初始化公安提供的其他类型诈骗网站及预警相关
        Map<String,Object> gaqtl = new LinkedHashMap<>();
        gaqtl.put("name","公安提供的其他类型诈骗网站及预警");
        gaqtl.put("website",null);
        gaqtl.put("warning",null);
        gaqtl.put("website_last_week",null);
        gaqtl.put("warning_last_week",null);

        //初始化各类型预警按地市统计相关
        Map<String,Object> glxyjadstj = new LinkedHashMap<>();
        glxyjadstj.put("name","各类型预警按地市统计");
        glxyjadstj.put("last_week_warning",null);
        glxyjadstj.put("city",null);

        result.getTable().add(dk_dbxykl);
        result.getTable().add(sdfll);
        result.getTable().add(szpl);
        result.getTable().add(xjETC);
        result.getTable().add(gaqtl);
        result.getTable().add(glxyjadstj);

        //初始化预警数据近四周变化趋势统计相关
        Map<String,Object> yjsjjszbhqstj = new LinkedHashMap<>();
        yjsjjszbhqstj.put("name","预警数据近四周变化趋势统计");
        yjsjjszbhqstj.put("header_name","预警总数");
        yjsjjszbhqstj.put("week1",null);
        yjsjjszbhqstj.put("week2",null);
        yjsjjszbhqstj.put("week3",null);
        yjsjjszbhqstj.put("week4",null);

        //初始化黑样本库统计分析相关
        Map<String,Object> hybktjfx = new LinkedHashMap<>();
        hybktjfx.put("name","黑样本库统计分析");
        hybktjfx.put("header_name","涉诈网站数");
        hybktjfx.put("history_sum",null);
        hybktjfx.put("week_sum",null);
        hybktjfx.put("week1",null);
        hybktjfx.put("week2",null);
        hybktjfx.put("week3",null);
        hybktjfx.put("week4",null);

        //初始化预警数据中近四周的活跃网站数量统计相关
        Map<String,Object> yjsjzjszdhywzsltj = new LinkedHashMap<>();
        yjsjzjszdhywzsltj.put("name","预警数据中近四周的活跃网站数量统计");
        yjsjzjszdhywzsltj.put("header_name","活跃涉诈网站");
        yjsjzjszdhywzsltj.put("week1",null);
        yjsjzjszdhywzsltj.put("week2",null);
        yjsjzjszdhywzsltj.put("week3",null);
        yjsjzjszdhywzsltj.put("week4",null);

        result.getTable_week().add(yjsjjszbhqstj);
        result.getTable_week().add(hybktjfx);
        result.getTable_week().add(yjsjzjszdhywzsltj);

        //初始化日志数据初始化，防止源数据丢失导致出现传递给data1-----data7之间任何数据缺失
        result.getLt().put("data1","0");
        result.getLt().put("data2","0");
        result.getLt().put("data3","0");
        result.getLt().put("data4","0");
        result.getLt().put("data5","0");
        result.getLt().put("data6","0");
        result.getLt().put("data7","0");

        result.getYd().put("data1","0");
        result.getYd().put("data2","0");
        result.getYd().put("data3","0");
        result.getYd().put("data4","0");
        result.getYd().put("data5","0");
        result.getYd().put("data6","0");
        result.getYd().put("data7","0");

        result.getDx().put("data1","0");
        result.getDx().put("data2","0");
        result.getDx().put("data3","0");
        result.getDx().put("data4","0");
        result.getDx().put("data5","0");
        result.getDx().put("data6","0");
        result.getDx().put("data7","0");

        result.getQuality().put("date",staticDate.getLogQuaDate());
        result.getQuality().put("total",null);
        result.getQuality().put("host_not_null",null);
        result.getQuality().put("ssl_sni_not_null",null);
    }

    @Override
    public void buildeSDFZData() throws Exception{
        logger.debug("======开始设置SDFZ相关数据============");
        //获取SDFZ数据源  数据
        Map<String, Object> sdfzResult = getSDFZResult(staticDate.getWarnStartDate(), staticDate.getWarnEndDate());
        //sdfzResult.putAll(getSDFZLogData(staticDate.getWarnStartDate(), staticDate.getWarnEndDate()));
        sdfzResult.putAll(select4weekwarn(staticDate));
        sdfzResult.putAll(select4weekwarnTrend(staticDate));
        //设置SDFZ数据源  数据
        dellSDFZData(sdfzResult,result);
        logger.debug("======SDFZ相关数据：{}",sdfzResult);
    }

    @Override
    public void buildeGDATA3Data() throws Exception{
        logger.debug("======开始设置GDATA3相关数据（此部分SQL全部改在HIVE上执行）============");
        //获取GDATA3数据源 数据
        Map<String, Object> gdata3Result = getGDATA3Result(staticDate.getWebsitStartDate(), staticDate.getWebsitEndDate());
        Map<String, List<Map<String, String>>> appNet = getAppNet(staticDate.getWarnStartDate(), staticDate.getWarnEndDate());

        gdata3Result.putAll(appNet);
        //设置GDATA3数据源  数据
        dellGDATA3Data(gdata3Result,result.getTable());
        logger.debug("======GDATA3相关数据：{}",gdata3Result);
    }

    @Override
    public void buildeHiveData() throws Exception{
        logger.debug("======开始设置Hive相关数据============");
        //获取HIVE数据源 数据
        Map<String, Object> getHiveResult = getHiveResult(staticDate.getWebsitStartDate(), staticDate.getWebsitEndDate());
        getHiveResult.putAll(getHybbzzl(staticDate.getWarnStartDate(), staticDate.getWarnEndDate()));
        getHiveResult.putAll(getHybkljzl());
        getHiveResult.putAll(getHybkFourWeek(staticDate.getWeek1Start(), staticDate.getWeek4End()));
        getHiveResult.putAll(getLogQuality(staticDate.getLogQuaDate()));
        getHiveResult.putAll(getHiveLogData(staticDate.getWarnStartDate(), staticDate.getWarnEndDate()));
        //设置HIVE数据源  数据
        dellGHData(getHiveResult,result);
        logger.debug("======HIVE相关数据：{}",getHiveResult);
    }

    @Override
    public void buildeYMFXYPData() throws Exception{
        logger.debug("======开始设置YMFXYP相关数据(此部分SQL全部改在HIVE上执行)============");
        //获取YMFXY数据源 数据
        Map<String, Object> getYMFXYPResult = getYMFXYPResult(staticDate.getWarnStartDate(), staticDate.getWarnEndDate());
        Map<String, List<Map<String, String>>> qqwx = getQQWX(staticDate.getWarnStartDate(), staticDate.getWarnEndDate());
        getYMFXYPResult.putAll(qqwx);
        //设置YMFXY数据源  数据
        dellYmfxypData(getYMFXYPResult,result.getTable());
        logger.debug("======YMFXYP相关数据：{}",getYMFXYPResult);
    }

    @Override
    public void buildeLastWeekData(WeeklyReportResult last) throws Exception {
        List<Map<String, Object>> last_table = last.getTable();
        List<Map<String, Object>> curr_table = result.getTable();
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
    }

    @Override
    public WeeklyReportResult getWeeklyReportResult() {
        return result;
    }


    /**
     * SDFZ数据源相关数据
     * @param ksrq
     * @param jsrq
     * @return
     */
    private Map<String,Object> getSDFZResult(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        Map<String,Object> sdfzResult = new HashMap<>();
        Map<String, Future<List<Map<String, Object>>>> futureMap = new HashMap<>();
        //根据ReportConstants.SDFZ_CONFIG的配制文件，调用f_tj_sql函数获取SQL并执行
        for(Map.Entry<String, String> entry : ReportConstants.SDFZ_CONFIG.entrySet()){
            Map<String,String> param = new HashMap<>();
            param.put("bbbm",ReportConstants.ALIZB_CODE);
            param.put("zbbm",entry.getKey());
            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
            try {
                param.put("ksrq",format2.format(format1.parse(ksrq)) + " 00:00:00");
                param.put("jsrq",format2.format(format1.parse(jsrq)) + " 23:59:59");
            } catch (Exception e) {
                e.printStackTrace();
            }
            weeklyReportOdpsService.callSqlFunction(param);
            logger.debug("======bbbm:{},zbbm:{},ksrq:{},jsrq:{}。获取的SQL:{}--{}",param.get("bbbm"),param.get("zbbm"),param.get("ksrq"),param.get("jsrq"),entry.getValue(),param.get("sql"));
            Future<List<Map<String, Object>>> sdfzSqlResult = weeklyReportOdpsService.getSDFZSqlResult(param.get("sql"));
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
    private Map<String,Object> getHiveLogData(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        Map<String,Object> logReciveResult = new HashMap<>();
        //移动
        Future<List<Map<String, Object>>> ydResult = weeklyReportOdpsService.getSDFZLogData(ksrq, jsrq,"移动");//山东移动
        Future<List<Map<String, Object>>> ltResult = weeklyReportOdpsService.getSDFZLogData(ksrq, jsrq,"联通");//山东联通
        Future<List<Map<String, Object>>> dxResult = weeklyReportOdpsService.getSDFZLogData(ksrq, jsrq,"电信");//山东电信

        logReciveResult.put("移动日志接入量",ydResult.get());
        logReciveResult.put("联通日志接入量",ltResult.get());
        logReciveResult.put("电信日志接入量",dxResult.get());
        return logReciveResult;
    }

    /**
     * 预警数据近一周的活跃网站数量统计
     * @param staticDate
     * @return
     */
    private Map<String,Object> select4weekwarn(StaticDate staticDate) throws ExecutionException, InterruptedException {
        Map<String,Object> sdfzResult = new HashMap<>();
        Future<List<Map<String, String>>> fourWeekWarn = weeklyReportOdpsService.select4weekwarn(staticDate);
        sdfzResult.put("预警数据近一周的活跃网站数量统计",fourWeekWarn.get());
        return sdfzResult;
    }

    /**
     * 预警数据近一周的活跃网站数量统计
     * @param staticDate
     * @return
     */
    private Map<String,Object> select4weekwarnTrend(StaticDate staticDate) throws ExecutionException, InterruptedException {
        Map<String,Object> sdfzResult = new HashMap<>();
        Future<List<Map<String, String>>> fourWeekWarn = weeklyReportOdpsService.select4weekwarnTrend(staticDate);
        sdfzResult.put("预警数据近四周变化趋势统计",fourWeekWarn.get());
        return sdfzResult;
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

//        //互联网日志
//        List<Map<String,Object>> hlyrzjrltj = (ArrayList<Map<String,Object>>) sdfzResult.get("互联网日志接入量统计");
//        Map<String, String> lt = weeklyReportResult.getLt();
//        Map<String, String> yd = weeklyReportResult.getYd();
//        Map<String, String> dx = weeklyReportResult.getDx();
//
//        int dateIndex = 1;
//        //处理实际数据天数少导致数据前移的问题
//        //1、根据统计开始日期初始化日期list
//        //2、根据日期list取对应数据填充，无则0
//        for(String date : staticDate.getDateList()){
//            for(Map<String,Object> map : hlyrzjrltj ){
//                if(StrUtil.equals(String.valueOf(map.get("type")),"山东电信") && StrUtil.equalsIgnoreCase(date,String.valueOf(map.get("rq")))){
//                    dx.put("data" + dateIndex,String.valueOf(map.get("nu")));
//                }
//                if(StrUtil.equals(String.valueOf(map.get("type")),"山东移动") && StrUtil.equalsIgnoreCase(date,String.valueOf(map.get("rq")))){
//                    yd.put("data" + dateIndex,String.valueOf(map.get("nu")));
//                }
//
//                if(StrUtil.equals(String.valueOf(map.get("type")),"山东联通") && StrUtil.equalsIgnoreCase(date,String.valueOf(map.get("rq")))){
//                    lt.put("data" + dateIndex,String.valueOf(map.get("nu")));
//                }
//            }
//            dateIndex++;
//        }


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
     * GDATA数据源相关数据
     * @param ksrq
     * @param jsrq
     * @return
     */
    private Map<String,Object> getGDATA3Result(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        Map<String,Object> gData3Result = new HashMap<>();
        Map<String,Future<List<Map<String, Object>>>> futureMap = new HashMap<>();
        //根据ReportConstants.GDATA3_CONFIG的配制文件，调用f_tj_sql函数获取SQL并执行
        for(Map.Entry<String, String> entry : ReportConstants.GDATA3_CONFIG.entrySet()){
            Map<String,String> param = new HashMap<>();
            param.put("bbbm",ReportConstants.ALIZB_CODE);
            param.put("zbbm",entry.getKey());
            param.put("ksrq",ksrq);
            param.put("jsrq",jsrq);
            weeklyReportOdpsService.callSqlFunction(param);
            Future<List<Map<String, Object>>> gdata3SqlResult = weeklyReportOdpsService.getGdata3SqlResult(param.get("sql"));
            futureMap.put(entry.getValue(),gdata3SqlResult);

        }
        for(Map.Entry<String, Future<List<Map<String, Object>>>> entry : futureMap.entrySet()){
            gData3Result.put(entry.getKey(),entry.getValue().get());
        }
        return gData3Result;
    }

    /**
     * GDATA数据源相关数据
     * @param ksrq
     * @param jsrq
     * @return
     */
    private Map<String,List<Map<String, String>>> getAppNet(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        Map<String,List<Map<String, String>>> futureMap = new HashMap<>();

        Future<List<Map<String, String>>> appNet = weeklyReportOdpsService.getAppNet(ksrq, jsrq);
        futureMap.put("appnet",appNet.get());
        return futureMap;
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

        //appnet  List<Map<String, String>>
        List<Map<String, String>> appNet = (List<Map<String, String>>)gdata3Result.get("appnet");
        logger.info("appnet统计：{}",appNet);
        for(Map<String,String> appNetData : appNet){
           if(StrUtil.equals(appNetData.get("source"),"netcent")){
               result.setNet(appNetData.get("numstr"));
           }
            if(StrUtil.equals(appNetData.get("source"),"app")){
                result.setApp(appNetData.get("numstr"));
            }

        }

    }

    /**
     * Hive数据源相关数据
     * @param ksrq
     * @param jsrq
     * @return
     */
    private Map<String,Object> getHiveResult(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        Map<String,Object> hiveResult = new HashMap<>();
        Map<String,Future<List<Map<String, Object>>>> futureMap = new HashMap<>();
        //根据ReportConstants.HIVE_CONFIG的配制文件，调用f_tj_sql函数获取SQL并执行
        for(Map.Entry<String, String> entry : ReportConstants.HIVE_CONFIG.entrySet()){
            Map<String,String> param = new HashMap<>();
            param.put("bbbm",ReportConstants.ALIZB_CODE);
            param.put("zbbm",entry.getKey());
            param.put("ksrq",ksrq);
            param.put("jsrq",jsrq);
            weeklyReportOdpsService.callSqlFunction(param);
            logger.debug("======bbbm:{},zbbm:{},ksrq:{},jsrq:{}。获取的SQL:{}",param.get("bbbm"),param.get("zbbm"),param.get("ksrq"),param.get("jsrq"),param.get("sql"));
            Future<List<Map<String, Object>>> hiveSqlResult = weeklyReportOdpsService.getHiveSqlResult(param.get("sql"));
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
    private Map<String,Object> getHybbzzl(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        Map<String,Object> hiveResult = new HashMap<>();
        Future<Map<String, String>> hybbzzl = weeklyReportOdpsService.getHybbzzl(ksrq,jsrq);
        hiveResult.put("黑样本本周总量",hybbzzl.get());
        return hiveResult;
    }

    /**
     * 黑样本累计总量
     * @return
     */
    private Map<String,Object> getHybkljzl() throws ExecutionException, InterruptedException {
        Map<String,Object> hiveResult = new HashMap<>();
        Future<Map<String, String>> hybbzzl = weeklyReportOdpsService.getHybkljzl();
        hiveResult.put("黑样本累计总量",hybbzzl.get());
        return hiveResult;
    }

    /**
     * 黑样本库近四周数据
     * @return
     */
    private Map<String,Object> getHybkFourWeek(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        Map<String,Object> hiveResult = new HashMap<>();
        Future<List<Map<String, String>>> hybkFourWeek = weeklyReportOdpsService.getHybkFourWeek(ksrq,jsrq);
        hiveResult.put("黑样本库近四周数据",hybkFourWeek.get());
        return hiveResult;
    }

    /**
     * log日志统计
     * @return
     */
    private Map<String,Object> getLogQuality(String rq) throws ExecutionException, InterruptedException {

        Map<String,Object> logQuaRes = new HashMap<>();
        Future<List<Map<String, String>>> hiveLogData = weeklyReportOdpsService.getLogQuality(rq);
        logQuaRes.put("互联网日志质量统计",hiveLogData.get());
        logger.debug("互联网日志质量统计{}",hiveLogData.get());
        return logQuaRes;
    }

    /**
     * 处理Hive1.1.0数据源的数据
     * @param getHiveResult
     */
    private void dellGHData(Map<String, Object> getHiveResult,WeeklyReportResult weeklyReportResult){
        List<Map<String, Object>> table = weeklyReportResult.getTable();
        //虚假ETC专项
        Map<String, Object> xjETC = table.get(3);
        Map<String,Object> xjETCOldData = (LinkedHashMap)((ArrayList) getHiveResult.get("虚假ETC短信统计")).get(0);
        Map<String,Object> xjETCNewData = new LinkedHashMap<>();
        MapUtil.exchangeReportDate(xjETCOldData,xjETCNewData);
        xjETC.put("message",xjETCNewData);

        Map<String, String> hybzlData = (Map<String, String>)getHiveResult.get("黑样本累计总量");
        Map<String, String> hybbzzl = (Map<String, String>)getHiveResult.get("黑样本本周总量");
        List<Map<String, String>> yhbkjszsj = (List<Map<String, String>>)getHiveResult.get("黑样本库近四周数据");
        List<Map<String, Object>> table_week = weeklyReportResult.getTable_week();
        Map<String, Object> map = table_week.get(1);
        map.put("history_sum",hybzlData.get("nu"));
        map.put("week_sum",hybbzzl.get("nu"));
        int yhbkjszsj_Index = 1;
        for(Map<String, String> weekMap : yhbkjszsj){
            map.put("week" + yhbkjszsj_Index++,weekMap.get("nu"));
        }

        // 互联网日志接入量统计
        //互联网日志
        List<Map<String,String>> ltJrl = (ArrayList<Map<String,String>>) getHiveResult.get("移动日志接入量");
        List<Map<String,String>> ydJrl = (ArrayList<Map<String,String>>) getHiveResult.get("联通日志接入量");
        List<Map<String,String>> dxJrl = (ArrayList<Map<String,String>>) getHiveResult.get("电信日志接入量");

        Map<String, String> lt = weeklyReportResult.getLt();
        Map<String, String> yd = weeklyReportResult.getYd();
        Map<String, String> dx = weeklyReportResult.getDx();

        MapUtil.exchangeCol2DataXByDate(ltJrl,lt,staticDate.getWarnStartDate());
        MapUtil.exchangeCol2DataXByDate(ydJrl,yd,staticDate.getWarnStartDate());
        MapUtil.exchangeCol2DataXByDate(dxJrl,dx,staticDate.getWarnStartDate());

        int hlwrzzl = 0;
        for(Map.Entry<String,String> entry : lt.entrySet()){
            hlwrzzl = hlwrzzl + Integer.parseInt(entry.getValue());
        }
        for(Map.Entry<String,String> entry : yd.entrySet()){
            hlwrzzl = hlwrzzl + Integer.parseInt(entry.getValue());
        }
        for(Map.Entry<String,String> entry : dx.entrySet()){
            hlwrzzl = hlwrzzl + Integer.parseInt(entry.getValue());
        }
        weeklyReportResult.setHlwrzsjzl(String.valueOf(hlwrzzl));

        List<Map<String, String>> logQuality =  (List<Map<String, String>>)getHiveResult.get("互联网日志质量统计");
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
            double hostData3 = 0.0;


            for(Map<String,String> logData : logQuality){

                //全量数据
                if(StrUtil.equals(logData.get("num_type"),"全量数据")){


                    if(StrUtil.equals(logData.get("tablename"),"hlw_fz.t_logs")){
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

                //https
                //20220831换成http，https=总量-http
                if(StrUtil.equals(logData.get("num_type"),"host不为空")){


                    if(StrUtil.equals(logData.get("tablename"),"hlw_fz.t_logs")){
                        hostData1 = Double.parseDouble(logData.get("num"))/100000000;
                        String data1 = NumberUtil.roundStr(hostData1,2);
                        hostNotNullMap.put("data1", data1);
                    }else{
                        hostData3 =Double.parseDouble(logData.get("num"))/100000000;
                        String data3 = NumberUtil.roundStr(hostData3,2);
                        hostNotNullMap.put("data3", data3);
                    }
                    quality.put("host_not_null",hostNotNullMap);
                }



            }

            //http=总量-sni不为空
            //20220831https=总量-http
            sslData1 = totalData1 - hostData1;
            String data1 = NumberUtil.roundStr(sslData1,2);
            sslNotNullMap.put("data1", data1);
            quality.put("ssl_sni_not_null",sslNotNullMap);

            //过滤后的http = 总量 - sni不为空
            sslData3 = NumberUtil.round(totalData2 - hostData3,2).doubleValue();

            String hostData2 = "0.00";
            String hostData4 = "0.00";
            String sslData2 = "0.00";
            String sslData4 = "0.00";
            if (totalData1 != 0) {
                hostData2 = NumberUtil.roundStr(hostData1 / totalData1, 2);
                sslData2 = NumberUtil.roundStr(sslData1 / totalData1, 2);
            }
            if (totalData2 != 0) {

                hostData4 = NumberUtil.roundStr(hostData3 / totalData2, 2);
                sslData4 = NumberUtil.roundStr(sslData3 / totalData2, 2);
            }

            hostNotNullMap.put("data2",hostData2);
            //hostNotNullMap.put("data3",String.valueOf(hostData3));
            hostNotNullMap.put("data4",hostData4);

            sslNotNullMap.put("data2",sslData2);
            sslNotNullMap.put("data3",String.valueOf(sslData3));
            sslNotNullMap.put("data4",sslData4);
        }
    }

    /**
     * YMFXYP数据源相关数据
     * @param ksrq
     * @param jsrq
     * @return
     */
    public Map<String,Object> getYMFXYPResult(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        Map<String,Object> ymfxypResult = new HashMap<>();
        Map<String,Future<List<Map<String, Object>>>> futureMap = new HashMap<>();
        //根据ReportConstants.YMFXYP_CONFIG，调用f_tj_sql函数获取SQL并执行
        for(Map.Entry<String, String> entry : ReportConstants.YMFXYP_CONFIG.entrySet()){
            Map<String,String> param = new HashMap<>();
            param.put("bbbm",ReportConstants.ALIZB_CODE);
            param.put("zbbm",entry.getKey());
            param.put("ksrq",ksrq);
            param.put("jsrq",jsrq);
            weeklyReportOdpsService.callSqlFunction(param);
            logger.debug("======bbbm:{},zbbm:{},ksrq:{},jsrq:{}。获取的SQL:{}",param.get("bbbm"),param.get("zbbm"),param.get("ksrq"),param.get("jsrq"),param.get("sql"));
            Future<List<Map<String, Object>>> ymfxypSqlResult = weeklyReportOdpsService.getYMFXYPSqlResult(param.get("sql"));
            futureMap.put(entry.getValue(),ymfxypSqlResult);

        }
        for(Map.Entry<String, Future<List<Map<String, Object>>>> entry : futureMap.entrySet()){
            ymfxypResult.put(entry.getKey(),entry.getValue().get());
        }
        return ymfxypResult;
    }

    /**
     * YMFXYP数据源相关数据
     * @param ksrq
     * @param jsrq
     * @return
     */
    private Map<String,List<Map<String, String>>> getQQWX(String ksrq,String jsrq) throws ExecutionException, InterruptedException {
        Map<String,List<Map<String, String>>> futureMap = new HashMap<>();
        String ksrqFormat = DateUtil.format(DateUtil.parse(ksrq),"yyyy-MM-dd");
        String jsrqFormat = DateUtil.format(DateUtil.parse(jsrq),"yyyy-MM-dd");
        Future<List<Map<String, String>>> appNet = weeklyReportOdpsService.getQQWX(ksrqFormat, jsrqFormat);
        futureMap.put("qqwx",appNet.get());
        return futureMap;
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

        //qqwx
        List<Map<String, String>> qqwxList = (List<Map<String, String>>)ymfxypResult.get("qqwx");
        DateTime startDate = DateUtil.parse(staticDate.getWarnStartDate());
        List<Map<String,String>> qqMapList = new ArrayList<>();
        List<Map<String,String>> wxMapList = new ArrayList<>();
        for(Map<String,String> qqwxData : qqwxList){
            if(StrUtil.equalsIgnoreCase(qqwxData.get("type"),"qq")){
                Map<String,String> qqMap = new HashMap<>();
                qqMap.put("dt",qqwxData.get("dt"));
                qqMap.put("num",qqwxData.get("num"));
                qqMapList.add(qqMap);
            }

            if(StrUtil.equalsIgnoreCase(qqwxData.get("type"),"wchart")){
                Map<String,String> wxMap = new HashMap<>();
                wxMap.put("dt",qqwxData.get("dt"));
                wxMap.put("num",qqwxData.get("num"));
                wxMapList.add(wxMap);
            }
        }
        MapUtil.exchangeCol2DataXByDate(qqMapList,result.getQq_wx().get("qq"),staticDate.getWarnStartDate());
        MapUtil.exchangeCol2DataXByDate(wxMapList,result.getQq_wx().get("wx"),staticDate.getWarnStartDate());

    }

    /**
     * 反序列化上周数据
     * @return
     */
    private WeeklyReportResult getLastWeekReport(String date){
        //1、初始化相关类
        //统计时间
        if(StrUtil.isEmpty(date)){
            date = DateUtil.format(DateUtil.date(),"yyyyMMdd");
        }
        date = DateUtil.format((DateUtil.offsetDay(DateUtil.parse(date,"yyyyMMdd"),-7)),"yyyyMMdd");
        SerializableUtil<WeeklyReportResult> serializableUtil = new SerializableUtil<>();
        WeeklyReportResult weeklyReportResult = serializableUtil.deSerializableObjectFromFile(serializableFile+"."+date, new Class[]{WeeklyReportResult.class,List.class,byte[].class}, WeeklyReportResult.class);
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

package com.xuanhuo.common.core.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 统计报表常量
 */
public class ReportConstants {
    /**
     * 山东反诈数据库对应统计SQL配置
     */
    public static final Map<String,String> SDFZ_CONFIG = new HashMap<>();

    /**
     * 阿里ADS数据库对应统计SQL配置
     */
    public static final Map<String,String> GDATA3_CONFIG = new HashMap<>();

    /**
     * 域名分析研判数据库对应统计SQL配置
     */
    public static final Map<String,String> YMFXYP_CONFIG = new HashMap<>();

    /**
     * HIVE数据库对应统计SQL配置
     */
    public static final Map<String,String> HIVE_CONFIG = new HashMap<>();

    /**
     * 周报代码
     */
    public static final String ALIZB_CODE = "102";

    /**
     * 快报代码
     */
    public static final String ALIKB_CODE = "101";

    static{
        //周报编码：对应统计名称
        SDFZ_CONFIG.put("1022","贷款、代办信用卡类预警统计");
        SDFZ_CONFIG.put("1023","贷款、代办信用卡类预警地市统计");
        SDFZ_CONFIG.put("1025","刷单返利类预警统计");
        SDFZ_CONFIG.put("1026","刷单返利类预警地市统计");
        SDFZ_CONFIG.put("1028","杀猪盘类预警统计");
        SDFZ_CONFIG.put("1029","杀猪盘类预警地市统计");
        SDFZ_CONFIG.put("1031","虚假ETC预警统计");
        SDFZ_CONFIG.put("1034","公安其他类型预警统计");
        SDFZ_CONFIG.put("1037","杀猪盘类预警分类统计");
        SDFZ_CONFIG.put("1038","地市预警分类排序");


        //SQL数据源改成了HIVE
        GDATA3_CONFIG.put("1021","贷款、代办信用卡类网站统计");
        GDATA3_CONFIG.put("1024","刷单返利类网站统计");
        GDATA3_CONFIG.put("1027","杀猪盘类网站统计");
        GDATA3_CONFIG.put("1030","虚假ETC网站统计");
        GDATA3_CONFIG.put("1036","杀猪盘类网站分类统计");
        //SQL数据源改成了HIVE
        YMFXYP_CONFIG.put("1035","公安提供网站统计");

        HIVE_CONFIG.put("1033","虚假ETC短信统计");
    }

}

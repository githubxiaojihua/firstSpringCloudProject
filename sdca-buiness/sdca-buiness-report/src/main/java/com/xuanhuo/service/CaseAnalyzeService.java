package com.xuanhuo.service;


import com.xuanhuo.pojo.StaticDate;
import com.xuanhuo.pojo.URIPojo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface CaseAnalyzeService {
    //分析URL是否有备案信息、是否可以访问、是否是涉诈网站
    Future<Map<String, Object>>  caeUrlAnalyze(String caseDate);
    //分析手机号总数及服务商
    Future<Map<String, Object>>  caePhoneAnalyze(String caseDate);
    //分析报案时间、预警时间
    void caseTimeAnalyze();
    //分析案件类型，及 是否app
    List<Map<String, String>> caseTypeAnalyze(String caseDate);
    //分析案发区域
    void caseLocationAnalyze();
    //分析涉案金额
    void caseMoneyAnalyze();
    //分析受害人、性别、年龄
    Future<Map<String,Map<String,Integer>>> casePeopleAnalyze(String caseDate) ;

    Future<Map<String,Integer>> getIPICPMessage(List<URIPojo> caseUrI);

    Future<List<URIPojo>> getCaseList(String caseDate);

    Future<Map<String, Object>> caseAppAnalyze(String caseDate);

    Future< List<Map<String, String>>> getTypeWarnCount(String caseDate) ;

}

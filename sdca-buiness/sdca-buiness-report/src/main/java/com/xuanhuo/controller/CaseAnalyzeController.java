package com.xuanhuo.controller;

import com.xuanhuo.common.core.controller.BaseController;
import com.xuanhuo.mapper.CaseAnalyzeMapper;
import com.xuanhuo.pojo.URIPojo;
import com.xuanhuo.service.CaseAnalyzeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 案件分析
 * 注意：案件分析需要分析IP及ICP需要在外网环境中运行，需要先将多数据源模块的配置文件设置成multiDataSource-dev.properties
 */
@RestController
@RequestMapping("/caseAnalyze")
public class CaseAnalyzeController extends BaseController {

    @Autowired
    CaseAnalyzeService caseAnalyzeService;
    @Resource
    CaseAnalyzeMapper caseAnalyzeMapper;


    @GetMapping(value = "/result/{caseDate}")
    public Map<String, Object> getPayment(@PathVariable("caseDate") String caseDate) throws ExecutionException, InterruptedException {
        Future<Map<String, Object>>  urlResultMap = caseAnalyzeService.caeUrlAnalyze(caseDate);
        Future<Map<String,Map<String,Integer>>> peopleResultMap = caseAnalyzeService.casePeopleAnalyze(caseDate);
        Future<Map<String, Object>> phoneFuture = caseAnalyzeService.caePhoneAnalyze(caseDate);
        Future<Map<String, Object>> appFuture = caseAnalyzeService.caseAppAnalyze(caseDate);
        List<Map<String, String>> caseTypemaps = caseAnalyzeService.caseTypeAnalyze(caseDate);
        Future<List<Map<String, String>>> typeWarnCount = caseAnalyzeService.getTypeWarnCount(caseDate);


        //分批解析IPICP
        List<URIPojo> caseUrI = caseAnalyzeService.getCaseList(caseDate).get();
        Map<String,Future<Map<String, Integer>>> ipICPResultMap = new HashMap<>();
        List<URIPojo> threadMession = new ArrayList<>();
        for(int i=0; i<caseUrI.size();i++){
            threadMession.add(caseUrI.get(i));
            if(i%99 == 0){
                ipICPResultMap.put(i+"",caseAnalyzeService.getIPICPMessage(threadMession));
                threadMession = new ArrayList<>();
            }
            if(i==caseUrI.size()-1){
                ipICPResultMap.put(i+"",caseAnalyzeService.getIPICPMessage(threadMession));
            }
        }
        Map<String,Integer> ipIcpMapResult = new HashMap<>();
        for(Future<Map<String, Integer>> v : ipICPResultMap.values()){
            ipIcpMapResult.putAll(v.get());
        }


        Map<String,Object> result = new LinkedHashMap<>();
        result.putAll(urlResultMap.get());
        result.put("ip属地分析",ipIcpMapResult);
        result.putAll(peopleResultMap.get());
        result.putAll(phoneFuture.get());
        result.putAll(appFuture.get());
        result.put("案件类型分析",caseTypemaps);

        return result;
    }

}

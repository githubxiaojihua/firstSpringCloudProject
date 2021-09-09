package com.xuanhuo.service.impl;

import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.xuanhuo.common.core.constant.DataSourceConstants;
import com.xuanhuo.common.core.domain.AjaxResult;
import com.xuanhuo.mapper.CaseAnalyzeMapper;
import com.xuanhuo.multidatasource.annotation.MultiDataSource;
import com.xuanhuo.pojo.CaseAqPojo;
import com.xuanhuo.pojo.PhonePojo;
import com.xuanhuo.pojo.URIPojo;
import com.xuanhuo.service.CaseAnalyzeService;
import com.xuanhuo.utils.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@Service
public class CaseAnalyzeServiceImp implements CaseAnalyzeService {
    protected static final Logger logger = LoggerFactory.getLogger(CaseAnalyzeServiceImp.class);
    @Resource
    CaseAnalyzeMapper caseAnalyzeMapper;

    @Autowired
    ThreadPoolExecutor threadPoolExecutor;

    @MultiDataSource(DataSourceConstants.DS_KEY_YMFXYP)
    @Async("taskExecutor")
    @Override
    public Future<Map<String, Object>> caeUrlAnalyze() {
        Map<String,Object> resultMap = new LinkedHashMap<>();
        //0、案件总数
        int caseCount = caseAnalyzeMapper.getCaseCount();
        resultMap.put("案件总数",String.valueOf(caseCount));

        //1、案件月份统计
        List<Map<String, String>> caseMonthCount = caseAnalyzeMapper.getCaseMonthCount();
        StringBuffer caseMonth = new StringBuffer();
        caseMonthCount.stream().forEach(map ->{
            caseMonth.append(map.get("mon")).append("月：").append(map.get("nu")).append("件").append(";");
        });
        resultMap.put("案件月份统计",caseMonth.toString());

        //2、分析ICP\ip\location
        List<URIPojo> caseUrI = caseAnalyzeMapper.getCaseUrl();
        int notIcpCount= 0;
        for(URIPojo uri : caseUrI){
            URIPojo icpUri = URIUtil.getIcp(uri);
            if(StrUtil.equals(icpUri.getIcp(),"无备案信息")){
                notIcpCount++;
            }
        }
        resultMap.put("URL总数",String.valueOf(caseUrI.size()));
        resultMap.put("有备案数",String.valueOf(caseUrI.size()-notIcpCount));
        resultMap.put("无备案数",String.valueOf(notIcpCount));

        //2、分析黑样本库收录
        int caseYjUrl = caseAnalyzeMapper.getCaseYjUrl();
        resultMap.put("涉诈URL数",String.valueOf(caseYjUrl));
        BigDecimal rate = NumberUtil.round((double)caseYjUrl/caseUrI.size(),4);
        resultMap.put("涉诈URL比例",String.valueOf(rate.doubleValue()));

        List<Map<String, String>> typeWarnCount = caseAnalyzeMapper.getTypeWarnCount();
        int typeCount = 0;
        for(Map<String, String> map:typeWarnCount){
            typeCount += Integer.valueOf(map.get("nu"));
        }
        for(Map<String, String> map:typeWarnCount){
            int dlNu = Integer.valueOf(map.get("nu"));
            double dlRate = (double)dlNu/typeCount;
            map.put("nu",String.valueOf(dlRate));
        }

        resultMap.put("收录类型占比：",typeWarnCount);

        //3、分析
        return new AsyncResult<>(resultMap);
    }

    @Async("taskExecutor")
    @Override
    public Future<Map<String,Integer>> getIPICPMessage(List<URIPojo> caseUrI){
        Map<String,Integer> locationCount = new HashMap<>();
        for(URIPojo uri : caseUrI){
            URIPojo ipUri = URIUtil.getIPbyUrl(uri);
            if(StrUtil.isNotEmpty(ipUri.getIp())){
                URIUtil.getIpCity(ipUri);
                int count_tmp = locationCount.get(ipUri.getLocal())==null?0:locationCount.get(ipUri.getLocal());
                locationCount.put(ipUri.getLocal(),count_tmp+1);
                logger.debug(ipUri.getLocal());
            }
        }
        return new AsyncResult<>(locationCount);
    }

    @Async("taskExecutor")
    public Map<String,Integer> getIPICP(URIPojo uri,Map<String,Integer> locationCount){
        URIPojo ipUri = URIUtil.getIPbyUrl(uri);
        if(StrUtil.isNotEmpty(ipUri.getIp())){
            URIUtil.getIpCity(ipUri);
            int count_tmp = locationCount.get(ipUri.getLocal())==null?0:locationCount.get(ipUri.getLocal());
            locationCount.put(ipUri.getLocal(),count_tmp+1);
            System.out.println(ipUri.getLocal());
        }
        return locationCount;
    }

    @MultiDataSource(DataSourceConstants.DS_KEY_YMFXYP)
    @Async("taskExecutor")
    @Override
    public Future<Map<String, Object>> caePhoneAnalyze() {
        Map<String,Object> resultMap = new LinkedHashMap<>();
        //1、手机总数
        List<PhonePojo> casePhone = caseAnalyzeMapper.getCasePhone();
        List<PhonePojo> newCollect = casePhone.stream().filter(new Predicate<PhonePojo>() {
            @Override
            public boolean test(PhonePojo phonePojo) {
                return isChinaPhoneLegal(phonePojo.getNo());
            }
        }).collect(Collectors.toList());
        resultMap.put("案件手机号总数",newCollect.size());
        List<Map<String, String>> casePhoneWarn = caseAnalyzeMapper.getCasePhoneWarn();
        int casePhoneWarnCount = caseAnalyzeMapper.getCasePhoneWarnCount();
        casePhoneWarn.sort(new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                int o1Mon = Integer.valueOf(o1.get("mon"));
                int o2Mon = Integer.valueOf(o2.get("mon"));
                return o1Mon>o2Mon?1:o1Mon<o2Mon?-1:0;
            }
        });
        StringBuffer casePhoneWarnSb = new StringBuffer();
        casePhoneWarn.stream().forEach(map ->{
            casePhoneWarnSb.append(map.get("mon")).append("月：").append(map.get("nu")).append(";");
        });
        resultMap.put("案件手机号预警按月统计",casePhoneWarnSb.toString());
        resultMap.put("手机号预警比例",String.valueOf(NumberUtil.round((double)casePhoneWarnCount/newCollect.size(),4)));

        return new AsyncResult<>(resultMap);
    }

    @MultiDataSource(DataSourceConstants.DS_KEY_YMFXYP)
    @Async("taskExecutor")
    @Override
    public Future<Map<String, Object>> caseAppAnalyze() {
        Map<String,Object> resultMap = new LinkedHashMap<>();
        List<Map<String, String>> caseApp = caseAnalyzeMapper.getCaseApp();
        caseApp.sort(new Comparator<Map<String, String>>() {
            @Override
            public int compare(Map<String, String> o1, Map<String, String> o2) {
                int o1Nu = Integer.valueOf(o1.get("nu"));
                int o2Nu = Integer.valueOf(o2.get("nu"));
                return o1Nu>o2Nu?1:o2Nu>o1Nu?-1:0;
            }
        });
        StringBuffer caseAppSb = new StringBuffer();
        caseApp.stream().forEach(map->{
            caseAppSb.append(map.get("apps")).append(":").append(map.get("nu")).append(";");
        });
        resultMap.put("案件涉及app",caseAppSb.toString());
        return new AsyncResult<>(resultMap);
    }

    @MultiDataSource(DataSourceConstants.DS_KEY_YMFXYP)
    @Async("taskExecutor")
    @Override
    public Future< List<Map<String, String>>> getTypeWarnCount() {
        List<Map<String, String>> typeWarnCount = caseAnalyzeMapper.getTypeWarnCount();
        return new AsyncResult<>(typeWarnCount);
    }

    @MultiDataSource(DataSourceConstants.DS_KEY_YMFXYP)
    @Override
    public Future<List<URIPojo>> getCaseList() {
        List<URIPojo> caseUrI = caseAnalyzeMapper.getCaseUrl();
        return new AsyncResult<>(caseUrI);
    }

    @Override
    public void caseTimeAnalyze() {

    }

    @MultiDataSource(DataSourceConstants.DS_KEY_YMFXYP)
    @Override
    public List<Map<String, String>> caseTypeAnalyze() {
        List<Map<String, String>> caseType = caseAnalyzeMapper.getCaseType();
        return caseType;
    }

    @Override
    public void caseLocationAnalyze() {

    }

    @Override
    public void caseMoneyAnalyze() {

    }

    @MultiDataSource(DataSourceConstants.DS_KEY_YMFXYP)
    @Async("taskExecutor")
    @Override
    public Future<Map<String,Map<String,Integer>>> casePeopleAnalyze() {
        List<CaseAqPojo> caseAq = caseAnalyzeMapper.getCaseAq();
        Map<String,Integer> genderCount = new HashMap<>();
        Map<String,Integer> ageCount = new HashMap<>();
        caseAq.stream().forEach(aq ->{
            System.out.println(aq.getJyaq());
            String idCard = getIDNo(aq.getJyaq());
            if(StrUtil.isNotEmpty(idCard) && IdcardUtil.isValidCard(idCard)){
                System.out.println(idCard);
                int gender = IdcardUtil.getGenderByIdCard(idCard);
                if(gender == 0){
                    int nvNU = genderCount.get("女")==null?0:genderCount.get("女");
                    genderCount.put("女",nvNU + 1);
                }else{
                    int naNU = genderCount.get("男")==null?0:genderCount.get("男");
                    genderCount.put("男",naNU + 1);
                }

                int ageByIdCard = IdcardUtil.getAgeByIdCard(idCard);
                if(ageByIdCard<18){
                    int wcnNU = ageCount.get("未成年")==null?0:ageCount.get("未成年");
                    ageCount.put("未成年",wcnNU+1);
                }else if(ageByIdCard>=18 && ageByIdCard<=40){
                    int wcnNU2 = ageCount.get("18到40岁")==null?0:ageCount.get("18到40岁");
                    ageCount.put("18到40岁",wcnNU2+1);
                }else{
                    int wcnNU3 = ageCount.get("40岁以上")==null?0:ageCount.get("40岁以上");
                    ageCount.put("40岁以上",wcnNU3+1);
                }
            }

        });
        Map<String,Map<String,Integer>> resultMap = new HashMap<>();
        resultMap.put("受害人性别分析：",genderCount);
        resultMap.put("受害人年龄分析：",ageCount);
        return  new AsyncResult<>(resultMap);
    }

    private String getIDNo(String content){
        String regex ="\\d{17}[\\d|x]|\\d{15}";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        if(m.find()){
            return m.group();
        }
        return "";
    }

    public boolean isChinaPhoneLegal(String str) throws PatternSyntaxException {
        // ^ 匹配输入字符串开始的位置
        // \d 匹配一个或多个数字，其中 \ 要转义，所以是 \\d
        // $ 匹配输入字符串结尾的位置
        String regExp = "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[3,5,6,7,8])" +
                "|(18[0-9])|(19[8,9]))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

}

package com.xuanhuo.utils;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xuanhuo.common.core.domain.AjaxResult;
import com.xuanhuo.constant.CaseAnalyzeConstant;
import com.xuanhuo.pojo.URIPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * URI工具类
 */
public class URIUtil {
    protected static final Logger logger = LoggerFactory.getLogger(URIUtil.class);

    /**
     * 根据URL获取备案信息
     * @param URI
     * @return
     */
    public static URIPojo getIcp(URIPojo URI){
        logger.debug("开始分析{}的备案信息",URI.getUrl());
        String path= CaseAnalyzeConstant.ICP_url +"?key="+CaseAnalyzeConstant.APK+"&domainName="+URI.getUrl();
        String result="";
        try{
            String html= HttpUtil.post(path, "");
            JSONObject jsonObject = JSONUtil.parseObj(html);//转换成json对象
            String code=jsonObject.getStr("StateCode");
            if("1".equals(code)){
                result=jsonObject.getJSONObject("Result").getStr("SiteLicense");
                URI.setIcp(result);
            }else{
                logger.warn("未获取到{}的备案信息，原因是{}",URI.getUrl(),jsonObject.getStr("Reason"));
                URI.setIcp("无备案信息");
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return URI;
    }

    /**
     * 根据URI获到IP
     * @param URI
     * @return
     */
    public static URIPojo getIPbyUrl(URIPojo URI){
        String ip="";
        try {
            InetAddress[] addresses = InetAddress.getAllByName(URI.getUrl());
            for (int i = 0; i < addresses.length; i++) {
                ip+=addresses[i].getHostAddress()+",";
            }
            URI.setIp(ip);
        } catch (UnknownHostException uhe) {
            URI.setIp("获取失败");
        }
        return URI;

    }

    /**
     * 根据URI获到IP
     * @return
     */
    public static URIPojo getIpCity(URIPojo uri) {
        String[] iplist = uri.getIp().split(",");
        String result = "";
        for (int i = 0; i < iplist.length; i++) {
            try {
                result = HttpUtil.post(CaseAnalyzeConstant.IP_url + iplist[i], "",1000);
                JSONObject jsonObject = JSONUtil.parseObj(result);//转换成json对象
                String code = jsonObject.getStr("code");
                if ("0".equals(code)) {
                    result = jsonObject.getJSONObject("data").getStr("country") + jsonObject.getJSONObject("data").getStr("region") + jsonObject.getJSONObject("data").getStr("city");
                } else {
                    result = jsonObject.getStr("msg");
                }
                result += ";";
                uri.setLocal(result);
            } catch (Exception e) {
                //e.printStackTrace();
                uri.setLocal("操作失败");
            }
        }
        return uri;
    }
}

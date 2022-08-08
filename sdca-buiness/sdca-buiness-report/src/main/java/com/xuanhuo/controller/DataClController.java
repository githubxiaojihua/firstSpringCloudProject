package com.xuanhuo.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.xuanhuo.common.core.controller.BaseController;
import com.xuanhuo.common.core.domain.AjaxResult;
import com.xuanhuo.common.core.utils.SerializableUtil;
import com.xuanhuo.common.reportResult.*;
import com.xuanhuo.pojo.StaticDate;
import com.xuanhuo.service.DataClService;
import com.xuanhuo.service.WeeklyReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/dataCl")
public class DataClController extends BaseController {

    @Autowired
    private DataClService dataClService;

    @GetMapping("/clSzzh")
    public AjaxResult clSzzh() throws ExecutionException, InterruptedException,Exception {
        List<Map<String, String>> szzh = dataClService.getSzzh(null);

        szzh.stream().forEach(szzhTmp ->{
            List<String> qqList = new ArrayList<>();
            List<String> weChatList = new ArrayList<>();
            String zhxx = szzhTmp.get("zhxx");
            JSONArray objects = JSONUtil.parseArray(zhxx);
            List<JSONObject> jsonObjects = JSONUtil.toList(objects, JSONObject.class);
            jsonObjects.stream().forEach(jsonObject -> {
                String type = (String)(jsonObject.get("accountType"));
                if(StrUtil.equalsIgnoreCase("QQ",type)){
                    qqList.add((String)(jsonObject.get("accountNo")));
                }
                if(StrUtil.equalsIgnoreCase("WECHAT",type)){
                    weChatList.add((String)(jsonObject.get("accountNo")));
                }
            });
            szzhTmp.put("QQ",JSONUtil.toJsonStr(qqList).replace("[","").replace("]","").replace("\"",""));
            szzhTmp.put("WECHAT",JSONUtil.toJsonStr(weChatList).replace("[","").replace("]","").replace("\"",""));
            dataClService.updateSzzh(szzhTmp);
        });

        return AjaxResult.success();
    }
}

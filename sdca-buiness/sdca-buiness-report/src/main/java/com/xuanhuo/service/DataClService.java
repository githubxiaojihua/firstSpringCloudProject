package com.xuanhuo.service;


import com.xuanhuo.pojo.StaticDate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface DataClService {

    List<Map<String,String>> getSzzh(Map param);

    int updateSzzh(Map<String,String> params);
}

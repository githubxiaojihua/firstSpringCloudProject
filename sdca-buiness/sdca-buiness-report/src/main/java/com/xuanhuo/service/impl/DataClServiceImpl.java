package com.xuanhuo.service.impl;

import com.xuanhuo.common.core.constant.DataSourceConstants;
import com.xuanhuo.mapper.CaseAnalyzeMapper;
import com.xuanhuo.mapper.DataClMapper;
import com.xuanhuo.multidatasource.annotation.MultiDataSource;
import com.xuanhuo.service.DataClService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class DataClServiceImpl implements DataClService {
    @Resource
    DataClMapper dataClMapper;

    @MultiDataSource(DataSourceConstants.DS_KEY_DATACL)
    @Override
    public List<Map<String, String>> getSzzh(Map param) {
        return dataClMapper.getSzzh(null);
    }

    @MultiDataSource(DataSourceConstants.DS_KEY_DATACL)
    @Override
    public List<Map<String, String>> getSzzhResult(Map param) {
        return  dataClMapper.getSzzhOrigin(null);
    }

    @MultiDataSource(DataSourceConstants.DS_KEY_DATACL)
    @Override
    public int updateSzzh(Map<String, String> params) {
        return dataClMapper.updateSzzh(params);
    }

    @MultiDataSource(DataSourceConstants.DS_KEY_DATACL)
    @Override
    public void insertSzzhResult(List<Map<String, Object>> list) {
        dataClMapper.insertSzzhResult(list);
    }
}

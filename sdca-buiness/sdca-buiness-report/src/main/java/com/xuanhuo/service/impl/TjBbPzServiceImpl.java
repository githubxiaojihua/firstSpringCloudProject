package com.xuanhuo.service.impl;

import com.xuanhuo.common.core.constant.DataSourceConstants;
import com.xuanhuo.domain.TjBbPz;
import com.xuanhuo.mapper.TjBbPzMapper;
import com.xuanhuo.multidatasource.annotation.MultiDataSource;
import com.xuanhuo.service.TjBbPzService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class TjBbPzServiceImpl implements TjBbPzService {

    @Resource
    private TjBbPzMapper tjBbPzMapper;


    @Override
    @MultiDataSource(DataSourceConstants.DS_KEY_SDFZ)
    public TjBbPz getTjBbPzByBm(String bm) {
        return tjBbPzMapper.getTjBbPzByBm(bm);
    }
}

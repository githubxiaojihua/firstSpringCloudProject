package com.xuanhuo.service.impl;

import com.xuanhuo.common.core.constant.DataSourceConstants;
import com.xuanhuo.common.core.constant.ReportConstants;
import com.xuanhuo.mapper.NativeSqlMapper;
import com.xuanhuo.mapper.WeeklyReportMapper;
import com.xuanhuo.multidatasource.annotation.MultiDataSource;
import com.xuanhuo.multidatasource.aop.MultiDataSourceAspect;
import com.xuanhuo.service.WeeklyReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class WeeklyReportServiceImpl implements WeeklyReportService {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    WeeklyReportMapper weeklyReportMapper;

    @Resource
    NativeSqlMapper nativeSqlMapper;

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * 执行f_tj_sql函数并获取相关SQL
     * @param param
     */
    @MultiDataSource(DataSourceConstants.DS_KEY_SDFZ)
    public void callSqlFunction(Map param) {
        weeklyReportMapper.callSqlFunction(param);
    }


    /**
     *  在SDFZ数据库执行相关SQL
     * @param sql
     * @return
     */
    @MultiDataSource(DataSourceConstants.DS_KEY_SDFZ)
    public List<Map<String, Object>> getSDFZSqlResult(String sql){
        return execNativeSql(sql);
    }

    /**
     *  在SDFZ数据库执行相关SQL
     * @param sql
     * @return
     */
    @MultiDataSource(DataSourceConstants.DS_KEY_GDATA3)
    public List<Map<String, Object>> getGdata3SqlResult(String sql){
        return execNativeSql(sql);
    }

    public List<Map<String,Object>> execNativeSql(String sql){
        logger.debug("======开始执行原生SQL");
        //logger.debug("jdbcTemplate====={}",jdbcTemplate);
        List<Map<String, Object>> result = nativeSqlMapper.nativeSelectSql(sql);
        //List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        // TODO: 此部分需要使用mybatis拦截器实现
        for(Map<String,Object> map : result){
            for(Map.Entry<String,Object> entry :  map.entrySet()){
                if(entry.getValue() instanceof byte[]){
                    entry.setValue(new String((byte[]) (entry.getValue())));
                }
            }
        }
        logger.debug("======原生SQL执行完成：{}",result);
        return result;
    }


}

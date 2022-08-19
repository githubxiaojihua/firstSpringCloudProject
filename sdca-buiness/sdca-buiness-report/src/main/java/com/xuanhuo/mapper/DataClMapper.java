package com.xuanhuo.mapper;

import com.xuanhuo.pojo.StaticDate;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DataClMapper {


	/**
	 * 查询涉诈账号
	 * @return
	 */
	List<Map<String,String>> getSzzh(Map param);

	List<Map<String,String>> getSzzhOrigin(Map param);

	int updateSzzh(Map<String,String> params);

	void insertSzzhResult(List<Map<String,Object>> list);
}

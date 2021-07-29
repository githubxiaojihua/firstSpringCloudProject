package com.xuanhuo.mapper;

import com.xuanhuo.domain.TjBbPz;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface WeeklyReportMapper {

	/**
	 * 调用mysql函数获取统计SQL
	 * @param param
	 * @return
	 */
	void callSqlFunction(Map param);
}

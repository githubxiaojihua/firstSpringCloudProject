package com.xuanhuo.mapper;

import com.xuanhuo.domain.TjBbPz;
import com.xuanhuo.pojo.StaticDate;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface WeeklyReportMapper {

	/**
	 * 调用mysql函数获取统计SQL
	 * @param param
	 * @return
	 */
	void callSqlFunction(Map param);

	/**
	 * 日志统计
	 * @param param
	 * @return
	 */
	List<Map<String, Object>> selectLogByService(Map param);

	/**
	 * 近四周统计
	 * @param staticDate
	 * @return
	 */
	List<Map<String, String>> select4weekwarn(StaticDate staticDate);

	/**
	 * 预警数据近四周变化趋势统计
	 * @param staticDate
	 * @return
	 */
	List<Map<String, String>> select4weekwarnTrend(StaticDate staticDate);

	/**
	 * 黑样本本周总量
	 * @return
	 */
	Map<String, String> getHybbzzl(Map param);

	/**
	 * 黑样本累计总量
	 * @return
	 */
	Map<String, String> getHybkljzl();

	/**
	 *黑样本库近四周数据
	 * @return
	 */
	List<Map<String,String>> getHybkFourWeek(Map param);
}

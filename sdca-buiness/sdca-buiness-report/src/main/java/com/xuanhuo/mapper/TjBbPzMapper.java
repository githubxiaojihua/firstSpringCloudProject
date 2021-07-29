package com.xuanhuo.mapper;

import com.xuanhuo.domain.TjBbPz;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

public interface TjBbPzMapper {

	/**
	 * 根据bm获取报表
	 * @param bm
	 * @return
	 */
	TjBbPz getTjBbPzByBm(@Param("bm") String bm);
}

package com.xuanhuo.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface NativeSqlMapper {
	ArrayList<Map<String,Object>> nativeSelectSql(@Param("_nativeSelectSql") String sql);
}

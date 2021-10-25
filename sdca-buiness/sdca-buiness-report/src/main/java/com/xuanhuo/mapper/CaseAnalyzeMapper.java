package com.xuanhuo.mapper;

import com.xuanhuo.pojo.CaseAqPojo;
import com.xuanhuo.pojo.PhonePojo;
import com.xuanhuo.pojo.URIPojo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CaseAnalyzeMapper {

	List<URIPojo> getCaseUrl(@Param("caseDate") String caseDate);
	int getCaseYjUrl(@Param("caseDate") String caseDate);
	int getCaseCount(@Param("caseDate") String caseDate);
    List<Map<String,String>> getCaseMonthCount();
	List<PhonePojo> getCasePhone(@Param("caseDate") String caseDate);
	List<Map<String,String>> getCasePhoneWarn();
	int getCasePhoneWarnCount(@Param("caseDate") String caseDate);
	List<CaseAqPojo> getCaseAq(@Param("caseDate") String caseDate);
	List<Map<String,String>> getCaseApp(@Param("caseDate") String caseDate);
	List<Map<String,String>> getCaseType(@Param("caseDate") String caseDate);
	List<Map<String,String>> getTypeWarnCount(@Param("caseDate") String caseDate);
}

package com.xuanhuo.mapper;

import com.xuanhuo.pojo.CaseAqPojo;
import com.xuanhuo.pojo.PhonePojo;
import com.xuanhuo.pojo.URIPojo;

import java.util.List;
import java.util.Map;

public interface CaseAnalyzeMapper {

	List<URIPojo> getCaseUrl();
	int getCaseYjUrl();
	int getCaseCount();
    List<Map<String,String>> getCaseMonthCount();
	List<PhonePojo> getCasePhone();
	List<Map<String,String>> getCasePhoneWarn();
	int getCasePhoneWarnCount();
	List<CaseAqPojo> getCaseAq();
	List<Map<String,String>> getCaseApp();
	List<Map<String,String>> getCaseType();
	List<Map<String,String>> getTypeWarnCount();
}

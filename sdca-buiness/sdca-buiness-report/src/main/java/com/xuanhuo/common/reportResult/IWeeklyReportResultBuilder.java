package com.xuanhuo.common.reportResult;

public interface IWeeklyReportResultBuilder {
    void initData();
    void buildeSDFZData() throws Exception;
    void buildeGDATA3Data() throws Exception;
    void buildeHiveData() throws Exception;
    void buildeYMFXYPData() throws Exception;
    void buildeLastWeekData(WeeklyReportResult last) throws Exception;

    WeeklyReportResult getWeeklyReportResult();
}

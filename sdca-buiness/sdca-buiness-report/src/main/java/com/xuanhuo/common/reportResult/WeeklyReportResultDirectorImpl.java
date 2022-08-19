package com.xuanhuo.common.reportResult;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
public class WeeklyReportResultDirectorImpl implements IWeeklyReportResultDirector{
    IWeeklyReportResultBuilder builder;
    public WeeklyReportResultDirectorImpl(IWeeklyReportResultBuilder builder){
        this.builder = builder;
    }
    @Override
    public void buildWeeklyReportResult() throws Exception{
        builder.initData();
        builder.buildeSDFZData();
        builder.buildeGDATA3Data();
        builder.buildeHiveData();
        builder.buildeYMFXYPData();

    }
}

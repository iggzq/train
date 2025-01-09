package com.study.train.batch.job;

import cn.hutool.core.date.DateUtil;
import com.study.train.batch.feign.BusinessFeign;
import jakarta.annotation.Resource;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.Date;

@DisallowConcurrentExecution
public class DailyTrainJob implements Job {

    @Resource
    BusinessFeign businessFeign;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        Date date = new Date();
        Date expireTime = DateUtil.offsetDay(date, 15).toJdkDate();
        businessFeign.genDaily(expireTime);
    }
}

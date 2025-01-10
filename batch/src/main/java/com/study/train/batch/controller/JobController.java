package com.study.train.batch.controller;

import com.study.train.batch.dto.CronJobDto;
import com.study.train.batch.resp.CronJobResp;
import com.study.train.common.resp.CommonResp;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping(value = "/admin/job")
public class JobController {

    private static final Logger LOG = LoggerFactory.getLogger(JobController.class);

    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    @Qualifier("schedulerFactoryBean")
    public void setSchedulerFactoryBean(SchedulerFactoryBean schedulerFactoryBean) {
        this.schedulerFactoryBean = schedulerFactoryBean;
    }

    @RequestMapping(value = "/run")
    public CommonResp<Object> run(@RequestBody CronJobDto cronJobReq) throws SchedulerException {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LOG.info("手动执行任务开始：{}, {}", jobClassName, jobGroupName);
        schedulerFactoryBean.getScheduler().triggerJob(JobKey.jobKey(jobClassName, jobGroupName));
        return new CommonResp<>();
    }

    @RequestMapping(value = "/add")
    public CommonResp<Object> add(@RequestBody CronJobDto cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        String cronExpression = cronJobReq.getCronExpression();
        String description = cronJobReq.getDescription();
        LOG.info("创建定时任务开始：{}，{}，{}，{}", jobClassName, jobGroupName, cronExpression, description);
        CommonResp<Object> commonResp = new CommonResp<>();

        try {
            // 通过SchedulerFactory获取一个调度器实例
            Scheduler schedule = schedulerFactoryBean.getScheduler();

            // 启动调度器
            schedule.start();

            //构建job信息
            JobDetail jobDetail = JobBuilder.newJob((Class<? extends Job>) Class.forName(jobClassName)).withIdentity(jobClassName, jobGroupName).build();

            //表达式调度构建器(即任务执行的时间)
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);

            //按新的cronExpression表达式构建一个新的trigger
            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobGroupName).withDescription(description).withSchedule(scheduleBuilder).build();

            schedule.scheduleJob(jobDetail, trigger);

        } catch (SchedulerException e) {
            LOG.error("创建定时任务失败:{}", String.valueOf(e));
            commonResp.setSuccess(false);
            commonResp.setMessage("创建定时任务失败:调度异常");
        } catch (ClassNotFoundException e) {
            LOG.error("创建定时任务失败:{}", String.valueOf(e));
            commonResp.setSuccess(false);
            commonResp.setMessage("创建定时任务失败：任务类不存在");
        }
        LOG.info("创建定时任务结束：{}", commonResp);
        return commonResp;
    }

    @RequestMapping(value = "/pause")
    public CommonResp<String> pause(@RequestBody CronJobDto cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LOG.info("暂停定时任务开始：{}，{}", jobClassName, jobGroupName);
        CommonResp<String> commonResp = new CommonResp<>();
        try {
            Scheduler schedule = schedulerFactoryBean.getScheduler();
            schedule.pauseJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            LOG.error("暂停定时任务失败:{}", String.valueOf(e));
            commonResp.setSuccess(false);
            commonResp.setMessage("暂停定时任务失败:调度异常");
        }
        LOG.info("暂停定时任务结束：{}", commonResp);
        return commonResp;
    }

    @RequestMapping(value = "/resume")
    public CommonResp<String> resume(@RequestBody CronJobDto cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LOG.info("重启定时任务开始：{}，{}", jobClassName, jobGroupName);
        CommonResp<String> commonResp = new CommonResp<>();
        try {
            Scheduler schedule = schedulerFactoryBean.getScheduler();
            schedule.resumeJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            LOG.error("重启定时任务失败:{}", String.valueOf(e));
            commonResp.setSuccess(false);
            commonResp.setMessage("重启定时任务失败:调度异常");
        }
        LOG.info("重启定时任务结束：{}", commonResp);
        return commonResp;
    }

    @RequestMapping(value = "/reschedule")
    public CommonResp<String> reschedule(@RequestBody CronJobDto cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        String cronExpression = cronJobReq.getCronExpression();
        String description = cronJobReq.getDescription();
        LOG.info("更新定时任务开始：{}，{}，{}，{}", jobClassName, jobGroupName, cronExpression, description);
        CommonResp<String> commonResp = new CommonResp<>();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(jobClassName, jobGroupName);
            // 表达式调度构建器
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression);
            CronTriggerImpl trigger1 = (CronTriggerImpl) scheduler.getTrigger(triggerKey);
            trigger1.setStartTime(new Date()); // 重新设置开始时间
            CronTrigger trigger = trigger1;

            // 按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withDescription(description).withSchedule(scheduleBuilder).build();

            // 按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
        } catch (Exception e) {
            LOG.error("更新定时任务失败:{}", String.valueOf(e));
            commonResp.setSuccess(false);
            commonResp.setMessage("更新定时任务失败:调度异常");
        }
        LOG.info("更新定时任务结束：{}", commonResp);
        return commonResp;
    }

    @RequestMapping(value = "/delete")
    public CommonResp<String> delete(@RequestBody CronJobDto cronJobReq) {
        String jobClassName = cronJobReq.getName();
        String jobGroupName = cronJobReq.getGroup();
        LOG.info("删除定时任务开始：{}，{}", jobClassName, jobGroupName);
        CommonResp<String> commonResp = new CommonResp<>();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            scheduler.pauseTrigger(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.unscheduleJob(TriggerKey.triggerKey(jobClassName, jobGroupName));
            scheduler.deleteJob(JobKey.jobKey(jobClassName, jobGroupName));
        } catch (SchedulerException e) {
            LOG.error("删除定时任务失败:{}", String.valueOf(e));
            commonResp.setSuccess(false);
            commonResp.setMessage("删除定时任务失败:调度异常");
        }
        LOG.info("删除定时任务结束：{}", commonResp);
        return commonResp;
    }

    @RequestMapping(value="/query")
    public CommonResp<List<CronJobResp>> query() {
        LOG.info("查看所有定时任务开始");
        CommonResp<List<CronJobResp>> commonResp = new CommonResp<>();
        List<CronJobResp> cronJobDtoList = new ArrayList<>();
        try {
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            for (String groupName : scheduler.getJobGroupNames()) {
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                    CronJobResp cronJobResp = new CronJobResp();
                    cronJobResp.setName(jobKey.getName());
                    cronJobResp.setGroup(jobKey.getGroup());

                    //get job's trigger
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    CronTrigger cronTrigger = (CronTrigger) triggers.get(0);
                    cronJobResp.setNextFireTime(cronTrigger.getNextFireTime());
                    cronJobResp.setPreFireTime(cronTrigger.getPreviousFireTime());
                    cronJobResp.setCronExpression(cronTrigger.getCronExpression());
                    cronJobResp.setDescription(cronTrigger.getDescription());
                    Trigger.TriggerState triggerState = scheduler.getTriggerState(cronTrigger.getKey());
                    cronJobResp.setState(triggerState.name());

                    cronJobDtoList.add(cronJobResp);
                }

            }
        } catch (SchedulerException e) {
            LOG.error("查看定时任务失败:{}", String.valueOf(e));
            commonResp.setSuccess(false);
            commonResp.setMessage("查看定时任务失败:调度异常");
        }
        commonResp.setContent(cronJobDtoList);
        LOG.info("查看定时任务结束：{}", commonResp);
        return commonResp;
    }

}
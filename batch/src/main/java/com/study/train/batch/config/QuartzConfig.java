package com.study.train.batch.config;

//@Configuration
//public class QuartzConfig {
//
//    //声明内容
//    @Bean
//    public JobDetail jobDetail() {
//        return JobBuilder.newJob(TestJob.class)
//                .withIdentity("TestJob", "test")
//                .storeDurably()
//                .build();
//    }
//
//    //声明触发器
//    @Bean
//    public Trigger trigger() {
//
//        return TriggerBuilder
//                .newTrigger()
//                .forJob(jobDetail())
//                .withIdentity("trigger", "trigger")
//                .startNow()
//                .withSchedule(CronScheduleBuilder.cronSchedule("*/2 * * * * ?"))
//                .build();
//    }
//}

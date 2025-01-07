package com.study.train.common.utils;

import cn.hutool.core.util.IdUtil;

public class SnowUtil {

    /*
        可以使用redis的分布式自增ID生成器,因为redis的分布式自增ID生成器是线程安全的
     */
    // 数据中心
    private static final long dataCenterId = 1;
    // 机器标识
    private static final long workerId = 1;

    public static long getSnowflakeNextId() {
        return IdUtil.getSnowflake(workerId, dataCenterId).nextId();
    }

    public static String getSnowflakeNextIdStr() {
        return IdUtil.getSnowflake(workerId, dataCenterId).nextIdStr();
    }
}

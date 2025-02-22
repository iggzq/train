package com.study.train.business.service;

import com.alibaba.fastjson.JSON;
import com.study.train.business.enums.RedisKeyPreEnum;
import com.study.train.business.enums.RocketMQTopicEnum;
import com.study.train.business.req.ConfirmOrderReq;
import com.study.train.common.context.LoginMemberHolder;
import com.study.train.common.exception.BusinessException;
import com.study.train.common.exception.BusinessExceptionEnum;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BeforeConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(BeforeConfirmOrderService.class);


    @Resource
    private LoginMemberHolder loginMemberHolder;

    @Resource
    private SkTokenService skTokenService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    private final RedissonClient redissonClient;

    @Autowired
    public BeforeConfirmOrderService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public void beforeSaveConfirmOrder(ConfirmOrderReq req) {
        // 检验令牌余量
        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), loginMemberHolder.getId());
        if (validSkToken) {
            LOG.info("令牌校验通过");
        } else {
            LOG.info("令牌校验不通过");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
        }

        // 添加分布式锁
        LOG.info("saveConfirm抢锁开始");
        String lockKey = RedisKeyPreEnum.CONFIRM_ORDER.getKey() + req.getTrainCode() + ":" + req.getDate();
        RLock lock = null;
        try {
            lock = redissonClient.getLock(lockKey);
            if (lock.tryLock(0, TimeUnit.SECONDS)) {
                LOG.info("{} 成功拿锁", Thread.currentThread().getName());
            } else {
                LOG.info("{} 抢锁失败", Thread.currentThread().getName());
                throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_LOCK_FAIL);
            }
            String reqJson = JSON.toJSONString(req);
            LOG.info("发送mq开始，消息：{}", reqJson);
            rocketMQTemplate.convertAndSend(
                    RocketMQTopicEnum.CONFIRM_ORDER.getKey(),
                    reqJson
            );
            LOG.info("发送mq结束");
        } catch (InterruptedException e) {
            LOG.error("购票异常", e);
        } finally {
            LOG.info("购票流程结束，释放锁!");
            if (null != lock && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}

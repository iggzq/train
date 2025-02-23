package com.study.train.business.service;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSON;
import com.study.train.business.domain.ConfirmOrder;
import com.study.train.business.enums.ConfirmOrderStatusEnum;
import com.study.train.business.enums.RocketMQTopicEnum;
import com.study.train.business.mapper.ConfirmOrderMapper;
import com.study.train.business.req.ConfirmOrderReq;
import com.study.train.common.context.LoginMemberHolder;
import com.study.train.common.exception.BusinessException;
import com.study.train.common.exception.BusinessExceptionEnum;
import com.study.train.common.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BeforeConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(BeforeConfirmOrderService.class);


    @Resource
    private LoginMemberHolder loginMemberHolder;

    @Resource
    private SkTokenService skTokenService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    public void beforeSaveConfirmOrder(ConfirmOrderReq req) {
        // 检验令牌余量
        boolean validSkToken = skTokenService.validSkToken(req.getDate(), req.getTrainCode(), loginMemberHolder.getId());
        if (validSkToken) {
            LOG.info("令牌校验通过");
        } else {
            LOG.info("令牌校验不通过");
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_SK_TOKEN_FAIL);
        }

        DateTime now = DateTime.now();
        long snowflakeNextId = SnowUtil.getSnowflakeNextId();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        BeanUtils.copyProperties(req, confirmOrder);
        confirmOrder.setId(snowflakeNextId);
        confirmOrder.setMemberId(loginMemberHolder.getId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setTickets(JSON.toJSONString(req.getTickets()));
        confirmOrderMapper.insert(confirmOrder);

        req.setLogId(MDC.get("LOG_ID"));
        req.setMemberId(loginMemberHolder.getId());
        String reqJson = JSON.toJSONString(req);
        LOG.info("发送mq开始，消息：{}", reqJson);
        rocketMQTemplate.convertAndSend(RocketMQTopicEnum.CONFIRM_ORDER.getKey(), reqJson);
        LOG.info("发送mq结束");
    }
}

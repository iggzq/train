package com.study.train.business.mq;

import com.alibaba.fastjson2.JSON;
import com.study.train.business.req.ConfirmOrderReq;
import com.study.train.business.service.ConfirmOrderService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
@RocketMQMessageListener(consumerGroup = "train", topic = "CONFIRM_ORDER")
public class ConfirmOrderConsumer implements RocketMQListener<MessageExt> {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderConsumer.class);

    @Resource
    private ConfirmOrderService confirmOrderService;

    @Override
    public void onMessage(MessageExt messageExt) {
        byte[] body = messageExt.getBody();
        String req = new String(body);
        ConfirmOrderReq confirmOrderReq = JSON.parseObject(req, ConfirmOrderReq.class);
        MDC.put("LOG_ID", confirmOrderReq.getLogId());
        LOG.info("ROCKETMQ收到消息，消费确认订单消息：{}", req);
        confirmOrderService.saveConfirm(confirmOrderReq);
    }
}

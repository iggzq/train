package com.study.train.business.service;


import cn.hutool.core.util.EnumUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.study.train.business.domain.ConfirmOrder;
import com.study.train.business.domain.ConfirmOrderExample;
import com.study.train.business.domain.DailyTrainStationSeat;
import com.study.train.business.domain.DailyTrainTicket;
import com.study.train.business.req.ConfirmOrderReq;
import com.study.train.business.req.ConfirmOrderTicketReq;
import com.study.train.business.enums.ConfirmOrderStatusEnum;
import com.study.train.business.enums.SeatTypeEnum;
import com.study.train.business.feign.MemberFeign;
import com.study.train.business.mapper.ConfirmOrderMapper;
import com.study.train.business.mapper.DailyTrainStationSeatMapper;
import com.study.train.business.mapper.customer.DailyTrainTicketMapperCust;
import com.study.train.common.exception.BusinessException;
import com.study.train.common.exception.BusinessExceptionEnum;
import com.study.train.common.req.MemberTicketReq;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    ConfirmOrderMapper confirmOrderMapper;

    @Resource
    private DailyTrainStationSeatMapper dailyTrainStationSeatMapper;

    @Resource
    private DailyTrainTicketMapperCust dailyTrainTicketMapperCust;

    @Resource
    private MemberFeign memberFeign;


    @Async
    public void setPaymentStatusWithExpiration(String orderId, Object value, int expireTimeInSeconds, List<DailyTrainStationSeat> finalSeatList, DailyTrainTicket dailyTrainTicket, ConfirmOrderReq confirmOrderReq, List<MemberTicketReq> memberTicketReqs) throws JsonProcessingException {
        // 设置Redis键值对并设置过期时间
        redisTemplate.opsForValue().set(orderId, value, expireTimeInSeconds + 1, TimeUnit.MINUTES);

        // 使用异步任务来定时检查未支付的订单并恢复MySQL的值
        checkAndRestorePaymentStatusFromDatabase(orderId, finalSeatList, dailyTrainTicket, confirmOrderReq, memberTicketReqs);
    }

    @Async
    protected void checkAndRestorePaymentStatusFromDatabase(String orderId, List<DailyTrainStationSeat> finalSeatList, DailyTrainTicket dailyTrainTicket, ConfirmOrderReq confirmOrderReq, List<MemberTicketReq> memberTicketReqs) throws JsonProcessingException {
        // 等待一段时间
        try {
//            Thread.sleep(900);
            //等待15分钟，即超时
            Thread.sleep(900000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        // 查询MySQL中该订单的支付状态
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.createCriteria().andIdEqualTo(Long.valueOf(orderId));
        ConfirmOrder confirmOrder = confirmOrderMapper.selectByExample(confirmOrderExample).get(0);
        if (confirmOrder.getStatus().equals("P")) {
            //到时间还未支付
            Object delete = redisTemplate.opsForValue().get(orderId);
            if (delete != null) {
                //修改回票数
                increaseTicketNum(confirmOrderReq, dailyTrainTicket);
                //修改订单状态
                confirmOrder.setStatus("C");
                confirmOrder.setUpdateTime(new Date());
                confirmOrderMapper.updateByPrimaryKeySelective(confirmOrder);
                //恢复具体的座位销售情况
                for (int j = 0; j < finalSeatList.size(); j++) {
                    DailyTrainStationSeat dailyTrainSeat = finalSeatList.get(j);
                    DailyTrainStationSeat updateSeat = new DailyTrainStationSeat();
                    updateSeat.setId(dailyTrainSeat.getId());
                    updateSeat.setSell(dailyTrainSeat.getSell());
                    updateSeat.setUpdateTime(new Date());
                    dailyTrainStationSeatMapper.updateByPrimaryKeySelective(updateSeat);
                    Integer startIndex = dailyTrainTicket.getStartIndex();
                    Integer endIndex = dailyTrainTicket.getEndIndex();
                    char[] charArray = updateSeat.getSell().toCharArray();
                    Integer maxStartIndex = endIndex - 1;
                    Integer minEndIndex = startIndex + 1;
                    int minStartIndex = 0;
                    for (int i = startIndex - 1; i >= 0; i--) {
                        char aChar = charArray[i];
                        if (aChar == '1') {
                            minStartIndex = i + 1;
                            break;
                        }
                    }
                    int maxEndIndex = updateSeat.getSell().length() + 1;
                    for (int i = endIndex; i < updateSeat.getSell().length(); i++) {
                        char aChar = charArray[i];
                        if (aChar == '1') {
                            maxEndIndex = i;
                            break;
                        }
                    }
                    dailyTrainTicketMapperCust.recoveryCountBySell(
                            dailyTrainSeat.getDate(),
                            dailyTrainSeat.getTrainCode(),
                            dailyTrainSeat.getSeatType(),
                            minStartIndex,
                            maxStartIndex,
                            minEndIndex,
                            maxEndIndex
                    );


                }
                // 调用会员服务接口，修改会员票状态为取消
                for (MemberTicketReq memberTicketReq : memberTicketReqs) {
                    memberTicketReq.setStatus(ConfirmOrderStatusEnum.CANCEL.getCode());
                    memberFeign.update(memberTicketReq);
                }
            }
        } else {
            //到时已支付，则删去redis中的键值对
            redisTemplate.delete(orderId);
        }

    }

    public void increaseTicketNum(ConfirmOrderReq confirmOrderReq, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketReq ticketReq : confirmOrderReq.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getKey, seatTypeCode);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() + 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() + 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() + 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() + 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
            }
        }
    }


}

package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.*;
import com.study.train.business.enums.ConfirmOrderStatusEnum;
import com.study.train.business.enums.SeatTypeEnum;
import com.study.train.business.mapper.ConfirmOrderMapper;
import com.study.train.business.req.*;
import com.study.train.business.resp.ConfirmOrderQueryResp;
import com.study.train.common.context.LoginMemberHolder;
import com.study.train.common.exception.BusinessException;
import com.study.train.common.exception.BusinessExceptionEnum;
import com.study.train.common.req.MemberTicketReq;
import com.study.train.common.resp.PageResp;
import com.study.train.common.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.apache.seata.core.context.RootContext;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Service
public class ConfirmOrderService {

    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    private LoginMemberHolder loginMemberHolder;

    @Resource
    ConfirmOrderMapper confirmOrderMapper;

    @Resource
    DailyTrainTicketService dailyTrainTicketService;

    @Resource
    DailyTrainStationCarriageService dailyTrainStationCarriageService;

    @Resource
    private DailyTrainStationSeatService dailyTrainStationSeatService;

    @Resource
    private AfterConfirmOrderService afterConfirmOrderService;

    @Resource
    private PaymentService paymentService;


    public void save(ConfirmOrderSaveReq confirmOrderSaveReq) {
        DateTime now = new DateTime();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(confirmOrderSaveReq, ConfirmOrder.class);
        if (ObjectUtil.isNull(confirmOrder.getId())) {
            confirmOrder.setId(SnowUtil.getSnowflakeNextId());
            confirmOrder.setCreateTime(now);
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.insert(confirmOrder);
        } else {
            confirmOrder.setUpdateTime(now);
            confirmOrderMapper.updateByPrimaryKey(confirmOrder);
        }

    }

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryReq req) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();

        LOG.info("查询页码：{}", req.getPage());
        LOG.info("每页条数：{}", req.getSize());
        PageHelper.startPage(req.getPage(), req.getSize());
        List<ConfirmOrder> confirmOrders = confirmOrderMapper.selectByExample(confirmOrderExample);

        PageInfo<ConfirmOrder> pageInfo = new PageInfo<>(confirmOrders);
        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<ConfirmOrderQueryResp> confirmOrderQueryResps = BeanUtil.copyToList(confirmOrders, ConfirmOrderQueryResp.class);
        PageResp<ConfirmOrderQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setData(confirmOrderQueryResps);

        return pageResp;
    }

    public void delete(Long id) {
        confirmOrderMapper.deleteByPrimaryKey(id);
    }

    public Long getExpireTime(ConfirmOrderReq confirmOrderReq) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.createCriteria().andMemberIdEqualTo(loginMemberHolder.getId()).andTrainCodeEqualTo(confirmOrderReq.getTrainCode()).andDateEqualTo(confirmOrderReq.getDate());
        Date createTime = confirmOrderMapper.selectByExample(confirmOrderExample).get(0).getCreateTime();
        System.out.println(createTime);
        LocalDateTime now = LocalDateTimeUtil.now();
        System.out.println(now);
        Instant instant = createTime.toInstant();
        LocalDateTime localCreateTime = LocalDateTime.ofInstant(instant, TimeZone.getTimeZone("GMT+8").toZoneId());
        System.out.println(localCreateTime);
        localCreateTime = localCreateTime.plusMinutes(15);
        System.out.println(localCreateTime);

        return LocalDateTimeUtil.between(now, localCreateTime).getSeconds();
    }


    @GlobalTransactional
    public TicketPayReq saveConfirm(ConfirmOrderReq req) {
        LOG.info("seata全局事务ID: {}", RootContext.getXID());
        // 此处省略了业务校验，如车次是否存在
        // 1.检查该乘客是否已经下过单,每个日期的每个车次用户只能下一张单
        List<ConfirmOrderTicketReq> tickets = req.getTickets();
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.createCriteria()
                .andStatusNotEqualTo(ConfirmOrderStatusEnum.CANCEL.getCode())
                .andStatusNotEqualTo(ConfirmOrderStatusEnum.FAILURE.getCode())
                .andDateEqualTo(req.getDate()).andMemberIdEqualTo(loginMemberHolder.getId())
                .andTrainCodeEqualTo(req.getTrainCode());
        List<ConfirmOrder> confirmOrders = confirmOrderMapper.selectByExample(confirmOrderExample);
        if (!confirmOrders.isEmpty()) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_ALREADY_EXIST);
        }
        // 2.创建订单对象，并保存订单到订单信息表
        Date date = req.getDate();
        String trainCode = req.getTrainCode();
        String start = req.getStart();
        String end = req.getEnd();
        DateTime now = DateTime.now();
        long snowflakeNextId = SnowUtil.getSnowflakeNextId();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        BeanUtils.copyProperties(req, confirmOrder);
        confirmOrder.setId(snowflakeNextId);
        confirmOrder.setMemberId(loginMemberHolder.getId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.PENDING.getCode());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setTickets(JSON.toJSONString(tickets));
        confirmOrderMapper.insert(confirmOrder);

        // 3.查询票余量，判断是否可以购买,若不可以，抛出异常，若可以，则更新票余量
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        Float totalMoney = reduceTicketNum(req, dailyTrainTicket);

        // 4.创建最终选座对象，用以保存选座结果
        List<DailyTrainStationSeat> finalSeatList = new ArrayList<>();
        for (ConfirmOrderTicketReq ticket : tickets) {
            if (StrUtil.isBlank(ticket.getSeatPosition())) {
                // 4.1 没有选座，进入自动选座逻辑
                LOG.info("本张票没有选座");
                getSeat(finalSeatList, date, trainCode, ticket.getSeatTypeCode(), null, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
            } else {
                // 4.2 有选座，进入指定选座逻辑
                LOG.info("本张票有选座");
                getSeat(finalSeatList, date, trainCode, ticket.getSeatTypeCode(), ticket.getSeatPosition(), dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
            }
        }

        LOG.info("最终选座结果：{}", finalSeatList);

        // 5.车次减去已购票数并增添用户购票结果
        try {
            List<MemberTicketReq> memberTicketReqs = afterConfirmOrderService.afterDoConfirm(dailyTrainTicket, finalSeatList, tickets, confirmOrder, totalMoney);
            paymentService.setPaymentStatusWithExpiration(String.valueOf(confirmOrder.getId()), confirmOrder, 5, finalSeatList, dailyTrainTicket, req, memberTicketReqs);
        } catch (Exception e) {
            LOG.error("保存购票信息失败", e);
            throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_EXCEPTION);
        }
        // 6.设置支付过期时间,若订单未支付，则恢复余票
        // 7.返回支付信息,结束购票逻辑
        TicketPayReq ticketPayReq = new TicketPayReq();
        ticketPayReq.setAmount(String.valueOf(totalMoney));
        ticketPayReq.setTradeName("车票");
        ticketPayReq.setTradeNum(String.valueOf(snowflakeNextId));

        return ticketPayReq;
    }

    private void getSeat(List<DailyTrainStationSeat> finalSeatList, Date date, String reqTrainCode, String reqCarriageType, String reqSeatPosition, Integer startIndex, Integer endIndex) {
        // 1.创建座位列表，用于保存选座结果
        List<DailyTrainStationSeat> getSeatList = new ArrayList<>();
        // 2.查询所有符合条件的车厢,因为分一等车厢，二等车厢
        List<DailyTrainStationCarriage> dailyTrainStationCarriages = dailyTrainStationCarriageService.selectBySeatType(date, reqTrainCode, reqCarriageType);
        LOG.info("共查询{}个符合条件的车厢", dailyTrainStationCarriages.size());
        // 3.遍历所有符合条件的车厢，从车厢中选座位
        for (DailyTrainStationCarriage dailyTrainStationCarriage : dailyTrainStationCarriages) {
            getSeatList.clear();
            LOG.info("开始从车厢{}选座", dailyTrainStationCarriage.getIndex());
            List<DailyTrainStationSeat> dailyTrainStationSeats = dailyTrainStationSeatService.selectByCarriage(date, reqTrainCode, dailyTrainStationCarriage.getIndex());
            // 4.遍历车厢中的座位，判断座位是否可以出售，若可以，则加入到getSeatList中
            // 5.根据用户选择的座位类型来决定i的初始值和跳转
            int start = 0;
            int jump = 1;
            if (reqCarriageType.equals(SeatTypeEnum.YDZ.getKey())) {
                if (ObjectUtil.isNotNull(reqSeatPosition)) {
                    jump = 4;
                    start = switch (reqSeatPosition) {
                        case "A" -> 0;
                        case "C" -> 1;
                        case "D" -> 2;
                        case "F" -> 3;
                        default -> start;
                    };
                }
            } else if (reqCarriageType.equals(SeatTypeEnum.EDZ.getKey())) {
                if (ObjectUtil.isNotNull(reqSeatPosition)) {
                    jump = 5;
                    start = switch (reqSeatPosition) {
                        case "A" -> 0;
                        case "B" -> 1;
                        case "C" -> 2;
                        case "D" -> 3;
                        case "F" -> 4;
                        default -> start;
                    };
                }
            }

            // 6.遍历车厢中的座位，判断座位是否可以出售，若可以，则加入到getSeatList中
            for (; start < dailyTrainStationSeats.size(); start += jump) {
                // 7.获取车厢中的座位对象
                DailyTrainStationSeat dailyTrainSeat = dailyTrainStationSeats.get(start);
                String col = dailyTrainSeat.getCol();
                // 8.
                boolean alreadySellFlag = false;
                for (DailyTrainStationSeat finalSeat : finalSeatList) {
                    if (finalSeat.getId().equals(dailyTrainSeat.getId())) {
                        alreadySellFlag = true;
                        break;
                    }
                }
                if (alreadySellFlag) {
                    LOG.info("座位{}已被出售", dailyTrainSeat.getCarriageSeatIndex());
                    continue;
                }

                if (StrUtil.isBlank(reqSeatPosition)) {
                    LOG.info("有选座");
                } else {
                    if (!reqSeatPosition.equals(col)) {
                        continue;
                    }
                }
                boolean canSellFlag = canSell(dailyTrainSeat, startIndex, endIndex);
                if (canSellFlag) {
                    LOG.info("可以选座");
                    getSeatList.add(dailyTrainSeat);
                } else {
                    continue;
                }
                finalSeatList.addAll(getSeatList);
                return;
            }
        }
    }

    private boolean canSell(DailyTrainStationSeat dailyTrainStationSeat, Integer startIndex, Integer endIndex) {
        String sell = dailyTrainStationSeat.getSell();
        String sellPart = sell.substring(startIndex - 1, endIndex - 1);
        if (Integer.parseInt(sellPart) > 0) {
            LOG.info("座位{}在本次车站区间{}~{}已售过票，不可选中该座位", dailyTrainStationSeat.getCarriageSeatIndex(), startIndex, endIndex);
            return false;
        } else {
            String sellPosition = sellPart.replaceAll("0", "1");
            sellPosition = StrUtil.fillBefore(sellPosition, '0', endIndex - 1);
            sellPosition = StrUtil.fillAfter(sellPosition, '0', sell.length());
            int newSell = NumberUtil.binaryToInt(sellPosition) | NumberUtil.binaryToInt(sell);
            String newSellStr = NumberUtil.getBinaryStr(newSell);
            newSellStr = StrUtil.fillBefore(newSellStr, '0', sell.length());
            dailyTrainStationSeat.setSell(newSellStr);
            LOG.info("座位{}被选中，原售票信息：{}，车站区间：{}~{}，即：{}，最终售票信息：{}", dailyTrainStationSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, sellPosition, newSell);
            return true;
        }

    }

    private Float reduceTicketNum(ConfirmOrderReq confirmOrderReq, DailyTrainTicket dailyTrainTicket) {
        float totalMoney = 0f;
        for (ConfirmOrderTicketReq ticketReq : confirmOrderReq.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getKey, seatTypeCode);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    totalMoney += dailyTrainTicket.getYdzPrice().floatValue();
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    totalMoney += dailyTrainTicket.getEdzPrice().floatValue();
                    dailyTrainTicket.setYdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    totalMoney += dailyTrainTicket.getRwPrice().floatValue();
                    dailyTrainTicket.setYdz(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    totalMoney += dailyTrainTicket.getYwPrice().floatValue();
                    dailyTrainTicket.setYdz(countLeft);
                }
            }
        }

        return totalMoney;
    }


}

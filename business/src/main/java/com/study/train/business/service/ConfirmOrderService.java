package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.*;
import com.study.train.business.dto.*;
import com.study.train.business.enums.ConfirmOrderStatusEnum;
import com.study.train.business.enums.SeatColEnum;
import com.study.train.business.enums.SeatTypeEnum;
import com.study.train.business.mapper.ConfirmOrderMapper;
import com.study.train.business.resp.ConfirmOrderQueryResp;
import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.exception.BusinessException;
import com.study.train.common.exception.BusinessExceptionEnum;
import com.study.train.common.req.MemberTicketReq;
import com.study.train.common.resp.PageResp;
import com.study.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    public void save(ConfirmOrderSaveDTO confirmOrderSaveDTO) {
        DateTime now = new DateTime();
        ConfirmOrder confirmOrder = BeanUtil.copyProperties(confirmOrderSaveDTO, ConfirmOrder.class);
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

    public PageResp<ConfirmOrderQueryResp> queryList(ConfirmOrderQueryDTO confirmOrderQueryDTO) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        ConfirmOrderExample.Criteria criteria = confirmOrderExample.createCriteria();
        PageHelper.startPage(confirmOrderQueryDTO.getPage(), confirmOrderQueryDTO.getSize());
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

    public Long getExpireTime(ConfirmOrderDTO confirmOrderDTO) {
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.createCriteria()
                .andMemberIdEqualTo(LoginMemberContext.getId())
                .andTrainCodeEqualTo(confirmOrderDTO.getTrainCode())
                .andDateEqualTo(confirmOrderDTO.getDate());
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


    public TicketPayDTO saveConfirm(ConfirmOrderDTO confirmOrderDTO) throws JsonProcessingException {
        //检查该乘客是否已经下过单,每个日期的每个车次用户只能下一张单
        List<ConfirmOrderTicketDTO> tickets = confirmOrderDTO.getTickets();
        ConfirmOrderExample confirmOrderExample = new ConfirmOrderExample();
        confirmOrderExample.createCriteria()
                .andDateEqualTo(confirmOrderDTO.getDate())
                .andMemberIdEqualTo(LoginMemberContext.getId())
                .andTrainCodeEqualTo(confirmOrderDTO.getTrainCode());
        List<ConfirmOrder> confirmOrders = confirmOrderMapper.selectByExample(confirmOrderExample);
        if (!confirmOrders.isEmpty()) {
            throw new BusinessException(BusinessExceptionEnum.ORDER_ALREADY_EXIST);
        }
        //保存订单到订单信息表
        Date date = confirmOrderDTO.getDate();
        String trainCode = confirmOrderDTO.getTrainCode();
        String start = confirmOrderDTO.getStart();
        String end = confirmOrderDTO.getEnd();
        DateTime now = DateTime.now();
        long snowflakeNextId = SnowUtil.getSnowflakeNextId();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(snowflakeNextId);
        confirmOrder.setMemberId(LoginMemberContext.getId());
        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(start);
        confirmOrder.setEnd(end);
        confirmOrder.setDailyTrainTicketId(confirmOrderDTO.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.PENDING.getCode());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setTickets(JSON.toJSONString(tickets));
        confirmOrderMapper.insert(confirmOrder);

        //查询票余量，判断是否可以购买,若不可以，抛出异常，若可以，则更新票余量
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        Float totalMoney = reduceTicketNum(confirmOrderDTO, dailyTrainTicket);
        //最终选座结果
        List<DailyTrainStationSeat> finalSeatList = new ArrayList<>();

        ConfirmOrderTicketDTO confirmOrderTicketDTO = tickets.get(0);
        if (StrUtil.isBlank(confirmOrderTicketDTO.getSeat())) {
            LOG.info("本次购票没有选座");
            for (ConfirmOrderTicketDTO ticket : tickets) {
                getSeat(finalSeatList, date, trainCode, ticket.getSeatTypeCode(), null, null, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
            }
        } else {
            LOG.info("本次购票有选座");
            List<SeatColEnum> seatColEnumList = SeatColEnum.getColsByType(confirmOrderTicketDTO.getSeatTypeCode());
            List<Integer> offsetList = getOffsetList(seatColEnumList, tickets);

            getSeat(finalSeatList, date, trainCode, confirmOrderTicketDTO.getSeatTypeCode(),
                    confirmOrderTicketDTO.getSeat().split("")[0], offsetList, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
        }
        List<MemberTicketReq> memberTicketReqs = afterConfirmOrderService.afterDoConfirm(dailyTrainTicket, finalSeatList, tickets,confirmOrder,totalMoney);

        paymentService.setPaymentStatusWithExpiration(String.valueOf(confirmOrder.getId()), confirmOrder, 60, finalSeatList, dailyTrainTicket, confirmOrderDTO, memberTicketReqs);
        TicketPayDTO ticketPayDTO = new TicketPayDTO();
        ticketPayDTO.setAmount(String.valueOf(totalMoney));
        ticketPayDTO.setTradeName("车票");
        ticketPayDTO.setTradeNum(String.valueOf(snowflakeNextId));
        return ticketPayDTO;
    }

    @NotNull
    private static List<Integer> getOffsetList(List<SeatColEnum> seatColEnumList, List<ConfirmOrderTicketDTO> tickets) {
        List<String> refreshSeatList = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            for (SeatColEnum seatColEnum : seatColEnumList) {
                refreshSeatList.add(seatColEnum.getKey() + i);
            }
        }
        //绝对偏移值，即：在参照座位列表中，找到当前座位的索引
        List<Integer> absoluteOffsetList = new ArrayList<>();
        List<Integer> offsetList = new ArrayList<>();
        for (ConfirmOrderTicketDTO ticket : tickets) {
            int index = refreshSeatList.indexOf(ticket.getSeat());
            absoluteOffsetList.add(index);
        }
        //相对偏移值，即：当前座位在参照座位列表中的索引
        for (Integer index : absoluteOffsetList) {
            int offset = index - absoluteOffsetList.get(0);
            offsetList.add(offset);
        }
        return offsetList;
    }

    private void getSeat(List<DailyTrainStationSeat> finalSeatList, Date date, String trainCode, String seatType, String column, List<Integer> offsetList, Integer startIndex, Integer endIndex) {
        List<DailyTrainStationSeat> getSeatList = new ArrayList<>();
        List<DailyTrainStationCarriage> dailyTrainStationCarriages = dailyTrainStationCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查询{}个符合条件的车厢", dailyTrainStationCarriages.size());
        for (DailyTrainStationCarriage dailyTrainStationCarriage : dailyTrainStationCarriages) {
            getSeatList.clear();
            LOG.info("开始从车厢{}选座", dailyTrainStationCarriage.getIndex());
            List<DailyTrainStationSeat> dailyTrainStationSeats = dailyTrainStationSeatService.selectByCarriage(date, trainCode, dailyTrainStationCarriage.getIndex());
            for (int i = 0; i < dailyTrainStationSeats.size(); i++) {
                DailyTrainStationSeat dailyTrainSeat = dailyTrainStationSeats.get(i);
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();
                String col = dailyTrainSeat.getCol();
                boolean alreadySellFlag = false;
                for (DailyTrainStationSeat finalSeat : finalSeatList) {
                    if (finalSeat.getId().equals(dailyTrainSeat.getId())) {
                        alreadySellFlag = true;
                        break;
                    }
                }
                if (alreadySellFlag) {
                    continue;
                }
                if (StrUtil.isBlank(column)) {
                    LOG.info("有选座");
                } else {
                    if (!column.equals(col)) {
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

                boolean isGetAllOffsetSeat = true;
                if (CollUtil.isNotEmpty(offsetList)) {
                    for (int j = 1; j < offsetList.size(); j++) {
                        Integer offset = offsetList.get(i);
                        int nextIndex = seatIndex + offset;
                        if (nextIndex >= dailyTrainStationSeats.size()) {
                            isGetAllOffsetSeat = false;
                            break;
                        }
                        DailyTrainStationSeat nextSeat = dailyTrainStationSeats.get(nextIndex);
                        boolean canSellNext = canSell(nextSeat, startIndex, endIndex);
                        if (!canSellNext) {
                            isGetAllOffsetSeat = false;
                            break;
                        } else {
                            getSeatList.add(nextSeat);
                        }
                    }
                }
                if (!isGetAllOffsetSeat) {
                    getSeatList.clear();
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
            LOG.info("座位{}被选中，原售票信息：{}，车站区间：{}~{}，即：{}，最终售票信息：{}"
                    , dailyTrainStationSeat.getCarriageSeatIndex(), sell, startIndex, endIndex, sellPosition, newSell);
            return true;
        }

    }

    private Float reduceTicketNum(ConfirmOrderDTO confirmOrderDTO, DailyTrainTicket dailyTrainTicket) {
        Float totalMoney = 0f;
        for (ConfirmOrderTicketDTO ticketReq : confirmOrderDTO.getTickets()) {
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

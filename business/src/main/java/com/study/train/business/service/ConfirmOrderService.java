package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.*;
import com.study.train.business.dto.ConfirmOrderDTO;
import com.study.train.business.dto.ConfirmOrderQueryDTO;
import com.study.train.business.dto.ConfirmOrderSaveDTO;
import com.study.train.business.dto.ConfirmOrderTicketDTO;
import com.study.train.business.enums.ConfirmOrderStatusEnum;
import com.study.train.business.enums.SeatColEnum;
import com.study.train.business.enums.SeatTypeEnum;
import com.study.train.business.mapper.ConfirmOrderMapper;
import com.study.train.business.resp.ConfirmOrderQueryResp;
import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.exception.BusinessException;
import com.study.train.common.exception.BusinessExceptionEnum;
import com.study.train.common.resp.PageResp;
import com.study.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


    public void saveConfirm(ConfirmOrderDTO confirmOrderDTO) {
        //保存订单到订单信息表
        Date date = confirmOrderDTO.getDate();
        String trainCode = confirmOrderDTO.getTrainCode();
        String start = confirmOrderDTO.getStart();
        String end = confirmOrderDTO.getEnd();
        DateTime now = DateTime.now();
        List<ConfirmOrderTicketDTO> tickets = confirmOrderDTO.getTickets();
        ConfirmOrder confirmOrder = new ConfirmOrder();
        confirmOrder.setId(SnowUtil.getSnowflakeNextId());
        confirmOrder.setMemberId(LoginMemberContext.getId());
        confirmOrder.setDate(date);
        confirmOrder.setTrainCode(trainCode);
        confirmOrder.setStart(start);
        confirmOrder.setEnd(end);
        confirmOrder.setDailyTrainTicketId(confirmOrderDTO.getDailyTrainTicketId());
        confirmOrder.setStatus(ConfirmOrderStatusEnum.INIT.getCode());
        confirmOrder.setCreateTime(now);
        confirmOrder.setUpdateTime(now);
        confirmOrder.setTickets(JSON.toJSONString(tickets));
        confirmOrderMapper.insert(confirmOrder);

        //查询票余量，判断是否可以购买,若不可以，抛出异常，若可以，则更新票余量
        DailyTrainTicket dailyTrainTicket = dailyTrainTicketService.selectByUnique(date, trainCode, start, end);
        reduceTicketNum(confirmOrderDTO, dailyTrainTicket);
        ConfirmOrderTicketDTO confirmOrderTicketDTO = tickets.get(0);
        if (StrUtil.isBlank(confirmOrderTicketDTO.getSeat())) {
            LOG.info("本次购票没有选座");
            for (ConfirmOrderTicketDTO ticket : tickets) {
                getSeat(date, trainCode, ticket.getSeatTypeCode(), null, null, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
            }

        } else {
            LOG.info("本次购票有选座");
            List<SeatColEnum> seatColEnumList = SeatColEnum.getColsByType(confirmOrderTicketDTO.getSeatTypeCode());
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

            getSeat(date, trainCode, confirmOrderTicketDTO.getSeatTypeCode(),
                    confirmOrderTicketDTO.getSeat().split("")[0], offsetList, dailyTrainTicket.getStartIndex(), dailyTrainTicket.getEndIndex());
        }

    }

    private void getSeat(Date date, String trainCode, String seatType, String column, List<Integer> offsetList, Integer startIndex, Integer endIndex) {
        List<DailyTrainStationCarriage> dailyTrainStationCarriages = dailyTrainStationCarriageService.selectBySeatType(date, trainCode, seatType);
        LOG.info("共查询{}个符合条件的车厢", dailyTrainStationCarriages.size());
        for (DailyTrainStationCarriage dailyTrainStationCarriage : dailyTrainStationCarriages) {
            LOG.info("开始从车厢{}选座", dailyTrainStationCarriage.getIndex());
            List<DailyTrainStationSeat> dailyTrainStationSeats = dailyTrainStationSeatService.selectByCarriage(date, trainCode, dailyTrainStationCarriage.getIndex());
            for (int i = 0; i < dailyTrainStationSeats.size(); i++) {
                DailyTrainStationSeat dailyTrainSeat = dailyTrainStationSeats.get(i);
                Integer seatIndex = dailyTrainSeat.getCarriageSeatIndex();
                String col = dailyTrainSeat.getCol();
                if (StrUtil.isBlank(column)) {
                    LOG.info("有选座");
                } else {
                    if (!column.equals(col)) {
                        continue;
                    }
                }
                boolean canSell = canSell(dailyTrainSeat, startIndex, endIndex);
                if (canSell) {
                    LOG.info("可以选座");
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
                        DailyTrainStationSeat nextDailyTrainStationSeat1 = dailyTrainStationSeats.get(nextIndex);
                        boolean canSellNext = canSell(dailyTrainSeat, startIndex, endIndex);
                        if (!canSellNext) {
                            isGetAllOffsetSeat = false;
                            break;
                        }
                    }
                }
                if(!isGetAllOffsetSeat){
                    continue;
                }

                return;
            }
        }
    }

    private boolean canSell(DailyTrainStationSeat dailyTrainStationSeat, Integer startIndex, Integer endIndex) {
        String sell = dailyTrainStationSeat.getSell();
        String sellPart = sell.substring(startIndex, endIndex);
        if (Integer.parseInt(sellPart) > 0) {
            return false;
        } else {
            String sellPosition = sellPart.replaceAll("0", "1");
            sellPosition = StrUtil.fillBefore(sellPosition, '0', endIndex);
            sellPosition = StrUtil.fillAfter(sellPosition, '0', sell.length());

            int newSell = NumberUtil.binaryToInt(sellPosition) | NumberUtil.binaryToInt(sell);
            String newSellStr = NumberUtil.getBinaryStr(newSell);
            newSellStr = StrUtil.fillBefore(newSellStr, '0', sell.length());
            dailyTrainStationSeat.setSell(newSellStr);
        }

    }

    private static void reduceTicketNum(ConfirmOrderDTO confirmOrderDTO, DailyTrainTicket dailyTrainTicket) {
        for (ConfirmOrderTicketDTO ticketReq : confirmOrderDTO.getTickets()) {
            String seatTypeCode = ticketReq.getSeatTypeCode();
            SeatTypeEnum seatTypeEnum = EnumUtil.getBy(SeatTypeEnum::getKey, seatTypeCode);
            switch (seatTypeEnum) {
                case YDZ -> {
                    int countLeft = dailyTrainTicket.getYdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case EDZ -> {
                    int countLeft = dailyTrainTicket.getEdz() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case RW -> {
                    int countLeft = dailyTrainTicket.getRw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
                case YW -> {
                    int countLeft = dailyTrainTicket.getYw() - 1;
                    if (countLeft < 0) {
                        throw new BusinessException(BusinessExceptionEnum.CONFIRM_ORDER_TICKET_COUNT_ERROR);
                    }
                    dailyTrainTicket.setYdz(countLeft);
                }
            }
        }
    }
}

package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.EnumUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.util.DateUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.DailyTrain;
import com.study.train.business.domain.DailyTrainTicket;
import com.study.train.business.domain.DailyTrainTicketExample;
import com.study.train.business.domain.TrainStation;
import com.study.train.business.enums.SeatTypeEnum;
import com.study.train.business.enums.TrainTypeEnum;
import com.study.train.business.mapper.DailyTrainTicketMapper;
import com.study.train.business.req.DailyTrainTicketQueryReq;
import com.study.train.business.req.DailyTrainTicketSaveReq;
import com.study.train.business.resp.DailyTrainTicketQueryResp;
import com.study.train.common.exception.BusinessException;
import com.study.train.common.exception.BusinessExceptionEnum;
import com.study.train.common.resp.PageResp;
import com.study.train.common.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
public class DailyTrainTicketService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainTicketService.class);

    @Resource
    DailyTrainTicketMapper dailyTrainTicketMapper;

    @Resource
    TrainStationService trainStationService;

    @Resource
    DailyTrainStationSeatService dailyTrainStationSeatService;

    public void save(DailyTrainTicketSaveReq dailyTrainTicketSaveReq) {
        DateTime now = new DateTime();
        DailyTrainTicket dailyTrainTicket = BeanUtil.copyProperties(dailyTrainTicketSaveReq, DailyTrainTicket.class);
        if (ObjectUtil.isNull(dailyTrainTicket.getId())) {
            dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainTicket.setCreateTime(now);
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.insert(dailyTrainTicket);
        } else {
            dailyTrainTicket.setUpdateTime(now);
            dailyTrainTicketMapper.updateByPrimaryKey(dailyTrainTicket);
        }

    }

    public PageResp<DailyTrainTicketQueryResp> queryList(DailyTrainTicketQueryReq dailyTrainTicketQueryReq) {
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        DailyTrainTicketExample.Criteria criteria = dailyTrainTicketExample.createCriteria();
        if (ObjectUtil.isNotNull(dailyTrainTicketQueryReq.getDate())) {
            //获取当前日期，精确到天
            Date date = new Date();
            String format = DateUtils.format(date, "yyyy-MM-dd");
            Date nowDay = DateUtils.parseDate(format);
            //判断选择日期是否在当前日期之前
            if (nowDay.after(dailyTrainTicketQueryReq.getDate())) {
                throw new BusinessException(BusinessExceptionEnum.USER_SELECT_DATE_BEFORE_NOW);
            }
            criteria.andDateEqualTo(dailyTrainTicketQueryReq.getDate());
        }
        if (ObjectUtil.isNotEmpty(dailyTrainTicketQueryReq.getTrainCode())) {
            criteria.andTrainCodeEqualTo(dailyTrainTicketQueryReq.getTrainCode());
        }
        if (ObjectUtil.isNotEmpty(dailyTrainTicketQueryReq.getStart())) {
            criteria.andStartEqualTo(dailyTrainTicketQueryReq.getStart());
        }
        if (ObjectUtil.isNotEmpty(dailyTrainTicketQueryReq.getEnd())) {
            criteria.andEndEqualTo(dailyTrainTicketQueryReq.getEnd());
        }
        PageHelper.startPage(dailyTrainTicketQueryReq.getPage(), dailyTrainTicketQueryReq.getSize());
        List<DailyTrainTicket> dailyTrainTickets = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);

        PageInfo<DailyTrainTicket> pageInfo = new PageInfo<>(dailyTrainTickets);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<DailyTrainTicketQueryResp> dailyTrainTicketQueryResps = BeanUtil.copyToList(dailyTrainTickets, DailyTrainTicketQueryResp.class);
        PageResp<DailyTrainTicketQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setData(dailyTrainTicketQueryResps);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainTicketMapper.deleteByPrimaryKey(id);
    }


    @Transactional
    public void genDaily(DailyTrain dailyTrain, Date date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的余票生成开始", DateTime.of(date).toString("yyyy-MM-dd"), trainCode);
        //删除该车次每日数据
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        dailyTrainTicketMapper.deleteByExample(dailyTrainTicketExample);

        //查询途径的车站信息
        List<TrainStation> trainStations = trainStationService.selectByTrainCode(trainCode);

        if (CollUtil.isEmpty(trainStations)) {
            LOG.info("该车次没有车站基础信息，生成该车次车票结束");
            return;
        }
//        int ydz = 0;
//        BigDecimal ydzPrice = new BigDecimal(0);
//        int edz = 0;
//        BigDecimal edzPrice;
//        int rw = 0;
//        BigDecimal rwPrice = new BigDecimal(0);
//        int yw = 0;
//        BigDecimal ywPrice = new BigDecimal(0);
//        List<DailyTrainStationSeat> dailyTrainStationSeats = dailyTrainStationSeatService.countSeat(date, trainCode);
//        for (DailyTrainStationSeat dailyTrainStationSeat : dailyTrainStationSeats) {
//            String seatType = dailyTrainStationSeat.getSeatType();
//            if (seatType.compareTo(SeatTypeEnum.YDZ.getKey()) == 0) {
//                ydz++;
//            } else if (seatType.compareTo(SeatTypeEnum.EDZ.getKey()) == 0) {
//                edz++;
//            } else if (seatType.compareTo(SeatTypeEnum.RW.getKey()) == 0) {
//                rw++;
//            } else if (seatType.compareTo(SeatTypeEnum.YW.getKey()) == 0) {
//                yw++;
//            }
//        }
        Date now = DateTime.now();
        // 1. 二层循环，i表示出发站，j表示到达站
        for (int i = 0; i < trainStations.size(); i++) {
            BigDecimal sumKm = new BigDecimal(0);
            // 2. 获取出发站对象
            TrainStation trainStationStart = trainStations.get(i);
            for (int j = i + 1; j < trainStations.size(); j++) {
                // 3. 获取到达站对象
                TrainStation trainStationEnd = trainStations.get(j);
                int ydz = dailyTrainStationSeatService.countSeat(date, trainCode, SeatTypeEnum.YDZ.getKey());
                int edz = dailyTrainStationSeatService.countSeat(date, trainCode, SeatTypeEnum.EDZ.getKey());
                int rw = dailyTrainStationSeatService.countSeat(date, trainCode, SeatTypeEnum.RW.getKey());
                int yw = dailyTrainStationSeatService.countSeat(date, trainCode, SeatTypeEnum.YW.getKey());
                sumKm = sumKm.add(trainStationEnd.getKm());
                BigDecimal priceRate = EnumUtil.getFieldBy(TrainTypeEnum::getPriceRate, TrainTypeEnum::getKey, dailyTrain.getType());
                BigDecimal ydzPrice = sumKm.multiply(SeatTypeEnum.YDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal edzPrice = sumKm.multiply(SeatTypeEnum.EDZ.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal rwPrice = sumKm.multiply(SeatTypeEnum.RW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                BigDecimal ywPrice = sumKm.multiply(SeatTypeEnum.YW.getPrice()).multiply(priceRate).setScale(2, RoundingMode.HALF_UP);
                // 4. 构建车票对象
                DailyTrainTicket dailyTrainTicket = new DailyTrainTicket();
                dailyTrainTicket.setId(SnowUtil.getSnowflakeNextId());
                dailyTrainTicket.setDate(date);
                dailyTrainTicket.setTrainCode(trainCode);
                dailyTrainTicket.setStart(trainStationStart.getName());
                dailyTrainTicket.setStartPinyin(trainStationStart.getNamePinyin());
                dailyTrainTicket.setStartTime(trainStationStart.getOutTime());
                dailyTrainTicket.setStartIndex(trainStationStart.getIndex());
                dailyTrainTicket.setEnd(trainStationEnd.getName());
                dailyTrainTicket.setEndPinyin(trainStationEnd.getNamePinyin());
                dailyTrainTicket.setEndIndex(trainStationEnd.getIndex());
                dailyTrainTicket.setEndTime(trainStationEnd.getInTime());
                dailyTrainTicket.setYdz(ydz);
                dailyTrainTicket.setYdzPrice(ydzPrice);
                dailyTrainTicket.setEdz(edz);
                dailyTrainTicket.setEdzPrice(edzPrice);
                dailyTrainTicket.setRw(rw);
                dailyTrainTicket.setRwPrice(rwPrice);
                dailyTrainTicket.setYw(yw);
                dailyTrainTicket.setYwPrice(ywPrice);
                dailyTrainTicket.setCreateTime(now);
                dailyTrainTicket.setUpdateTime(now);
                dailyTrainTicketMapper.insert(dailyTrainTicket);
            }
        }
        LOG.info("生成日期【{}】车次【{}】的余票生成结束", DateTime.of(date).toString("yyyy-MM-dd"), trainCode);
    }

    public DailyTrainTicket selectByUnique(Date date, String trainCode, String start, String end) {
        DailyTrainTicketExample dailyTrainTicketExample = new DailyTrainTicketExample();
        dailyTrainTicketExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode).andStartEqualTo(start).andEndEqualTo(end);
        List<DailyTrainTicket> dailyTrainTickets = dailyTrainTicketMapper.selectByExample(dailyTrainTicketExample);
        if (CollUtil.isEmpty(dailyTrainTickets)) {
            return null;
        } else {
            return dailyTrainTickets.get(0);
        }
    }


}

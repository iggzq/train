package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.DailyTrainStationSeat;
import com.study.train.business.domain.DailyTrainStationSeatExample;
import com.study.train.business.domain.TrainSeat;
import com.study.train.business.domain.TrainStation;
import com.study.train.business.domain.TrainSeatIsSoldOutAndData;
import com.study.train.business.mapper.DailyTrainStationSeatMapper;
import com.study.train.business.req.DailyTrainStationSeatQueryReq;
import com.study.train.business.req.DailyTrainStationSeatSaveReq;
import com.study.train.business.resp.DailyTrainStationSeatQueryResp;
import com.study.train.common.resp.PageResp;
import com.study.train.common.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class DailyTrainStationSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationSeatService.class);

    @Resource
    DailyTrainStationSeatMapper dailyTrainStationSeatMapper;

    @Resource
    TrainSeatService trainSeatService;

    @Resource
    TrainStationService trainStationService;

    public void save(DailyTrainStationSeatSaveReq dailyTrainStationSeatSaveReq) {
        DateTime now = new DateTime();
        DailyTrainStationSeat dailyTrainStationSeat = BeanUtil.copyProperties(dailyTrainStationSeatSaveReq, DailyTrainStationSeat.class);
        if (ObjectUtil.isNull(dailyTrainStationSeat.getId())) {
            dailyTrainStationSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStationSeat.setCreateTime(now);
            dailyTrainStationSeat.setUpdateTime(now);
            dailyTrainStationSeatMapper.insert(dailyTrainStationSeat);
        } else {
            dailyTrainStationSeat.setUpdateTime(now);
            dailyTrainStationSeatMapper.updateByPrimaryKey(dailyTrainStationSeat);
        }

    }

    public PageResp<DailyTrainStationSeatQueryResp> queryList(DailyTrainStationSeatQueryReq dailyTrainStationSeatQueryReq) {
        DailyTrainStationSeatExample dailyTrainStationSeatExample = new DailyTrainStationSeatExample();
        dailyTrainStationSeatExample.setOrderByClause("train_code asc,carriage_index asc,carriage_seat_index asc");
        DailyTrainStationSeatExample.Criteria criteria = dailyTrainStationSeatExample.createCriteria();
        if (ObjectUtil.isNotEmpty(dailyTrainStationSeatQueryReq.getTrainCode())) {
            criteria.andTrainCodeEqualTo(dailyTrainStationSeatQueryReq.getTrainCode());
        }
        PageHelper.startPage(dailyTrainStationSeatQueryReq.getPage(), dailyTrainStationSeatQueryReq.getSize());
        List<DailyTrainStationSeat> dailyTrainStationSeats = dailyTrainStationSeatMapper.selectByExample(dailyTrainStationSeatExample);

        PageInfo<DailyTrainStationSeat> pageInfo = new PageInfo<>(dailyTrainStationSeats);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<DailyTrainStationSeatQueryResp> dailyTrainStationSeatQueryResps = BeanUtil.copyToList(dailyTrainStationSeats, DailyTrainStationSeatQueryResp.class);
        PageResp<DailyTrainStationSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setData(dailyTrainStationSeatQueryResps);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainStationSeatMapper.deleteByPrimaryKey(id);
    }

    public void genDaily(Date date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的每日座位数据开始", date, trainCode);
        //删除该车次车站所有每日座位数据
        DailyTrainStationSeatExample dailyTrainStationSeatExample = new DailyTrainStationSeatExample();
        dailyTrainStationSeatExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        dailyTrainStationSeatMapper.deleteByExample(dailyTrainStationSeatExample);

        //获取该车次所有车站信息
        List<TrainStation> trainStations = trainStationService.selectByTrainCode(trainCode);
        String sell = StrUtil.fillBefore("", '0', trainStations.size() - 1);

        //查出某车次的所有座位信息
        List<TrainSeat> seatList = trainSeatService.selectByTrainCode(trainCode);
        if (CollUtil.isEmpty(seatList)) {
            LOG.info("该车次没有座位基础数据，生成该车次的每日座位信息结束");
            return;
        }

        // 创建每日车次的每日座位集合，以便批量插入
        List<DailyTrainStationSeat> dailyTrainStationSeats = new ArrayList<>();
        for (TrainSeat trainStationSeat : seatList) {
            Date now = DateTime.now();
            DailyTrainStationSeat dailyTrainStationSeat = BeanUtil.copyProperties(trainStationSeat, DailyTrainStationSeat.class);
            dailyTrainStationSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStationSeat.setCreateTime(now);
            dailyTrainStationSeat.setUpdateTime(now);
            dailyTrainStationSeat.setDate(date);
            dailyTrainStationSeat.setSell(sell);
            dailyTrainStationSeats.add(dailyTrainStationSeat);
        }
        dailyTrainStationSeatMapper.insertBatch(dailyTrainStationSeats);
        LOG.info("生成日期【{}】车次【{}】的每日座位数据结束", date, trainCode);
    }

    public int countSeat(Date date, String trainCode) {
        return countSeat(date, trainCode, null);
    }

    public int countSeat(Date date, String trainCode, String seatType) {
        DailyTrainStationSeatExample example = new DailyTrainStationSeatExample();
        DailyTrainStationSeatExample.Criteria criteria = example.createCriteria();
        criteria.andDateEqualTo(date)
                .andTrainCodeEqualTo(trainCode);
        if (StrUtil.isNotBlank(seatType)) {
            criteria.andSeatTypeEqualTo(seatType);
        }
        long l = dailyTrainStationSeatMapper.countByExample(example);
        if (l == 0L) {
            return -1;
        }
        return (int) l;
    }

    public TrainSeatIsSoldOutAndData selectByCarriage(Date date,String trainCode,Integer carriageIndex){
        TrainSeatIsSoldOutAndData trainSeatStatusAndData = new TrainSeatIsSoldOutAndData();
        DailyTrainStationSeatExample dailyTrainStationSeatExample = new DailyTrainStationSeatExample();
        dailyTrainStationSeatExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode).andCarriageIndexEqualTo(carriageIndex);
        trainSeatStatusAndData.setList(dailyTrainStationSeatMapper.selectByExample(dailyTrainStationSeatExample));
        trainSeatStatusAndData.setIsSoldOut(dailyTrainStationSeatMapper.isSoldOut(date,trainCode,carriageIndex));
        return trainSeatStatusAndData;
    }
}

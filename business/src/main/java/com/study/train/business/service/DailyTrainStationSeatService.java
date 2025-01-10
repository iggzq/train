package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.*;
import com.study.train.common.utils.SnowUtil;
import com.study.train.common.resp.PageResp;
import com.study.train.business.req.DailyTrainStationSeatQueryReq;
import com.study.train.business.req.DailyTrainStationSeatSaveReq;
import com.study.train.business.mapper.DailyTrainStationSeatMapper;
import com.study.train.business.resp.DailyTrainStationSeatQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
        LOG.info("生成日期【{}】车次【{}】的座位数据开始", date, trainCode);
        //删除该车次车站所有每日数据
        DailyTrainStationSeatExample dailyTrainStationSeatExample = new DailyTrainStationSeatExample();
        dailyTrainStationSeatExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        dailyTrainStationSeatMapper.deleteByExample(dailyTrainStationSeatExample);

        List<TrainStation> trainStations = trainStationService.selectByTrainCode(trainCode);
        String sell = StrUtil.fillBefore("", '0', trainStations.size() - 1);
        //查出某车次车站所有信息

        List<TrainSeat> trainSeats = trainSeatService.selectByTrainCode(trainCode);

        if (CollUtil.isEmpty(trainSeats)) {
            LOG.info("该车次没有车站基础信息，生成该车站车站信息结束");
            return;
        }
        for (TrainSeat trainStationSeat : trainSeats) {
            //生成该车次每日数据
            Date now = DateTime.now();
            DailyTrainStationSeat dailyTrainStationSeat = BeanUtil.copyProperties(trainStationSeat, DailyTrainStationSeat.class);
            dailyTrainStationSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStationSeat.setCreateTime(now);
            dailyTrainStationSeat.setUpdateTime(now);
            dailyTrainStationSeat.setDate(date);
            dailyTrainStationSeat.setSell(sell);
            dailyTrainStationSeatMapper.insert(dailyTrainStationSeat);
        }
        LOG.info("生成日期【{}】车次【{}】的座位数据结束", date, trainCode);
    }

    public List<DailyTrainStationSeat> countSeat(Date date,String trainCode){
        DailyTrainStationSeatExample dailyTrainStationSeatExample = new DailyTrainStationSeatExample();
        dailyTrainStationSeatExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        return dailyTrainStationSeatMapper.selectByExample(dailyTrainStationSeatExample);
    }

    public List<DailyTrainStationSeat> selectByCarriage(Date date,String trainCode,Integer carriageIndex){
        DailyTrainStationSeatExample dailyTrainStationSeatExample = new DailyTrainStationSeatExample();
        dailyTrainStationSeatExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode).andCarriageIndexEqualTo(carriageIndex);
        return dailyTrainStationSeatMapper.selectByExample(dailyTrainStationSeatExample);
    }
}

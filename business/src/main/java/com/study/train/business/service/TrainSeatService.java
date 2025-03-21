package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.TrainCarriage;
import com.study.train.business.domain.TrainSeat;
import com.study.train.business.domain.TrainSeatExample;
import com.study.train.business.enums.SeatColEnum;
import com.study.train.business.mapper.TrainSeatMapper;
import com.study.train.business.req.TrainSeatQueryReq;
import com.study.train.business.req.TrainSeatSaveReq;
import com.study.train.business.resp.TrainSeatQueryResp;
import com.study.train.common.resp.PageResp;
import com.study.train.common.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TrainSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);

    @Resource
    TrainSeatMapper trainSeatMapper;

    @Resource
    TrainCarriageService trainCarriageService;

    public void save(TrainSeatSaveReq trainSeatSaveReq) {
        DateTime now = DateTime.now();
        TrainSeat trainSeat = BeanUtil.copyProperties(trainSeatSaveReq, TrainSeat.class);
        if (ObjectUtil.isNull(trainSeat.getId())) {
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);
        } else {
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateByPrimaryKey(trainSeat);
        }

    }

    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryReq trainSeatQueryReq) {
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        if (ObjectUtil.isNotEmpty(trainSeatQueryReq.getTrainCode())) {
            criteria.andTrainCodeEqualTo(trainSeatQueryReq.getTrainCode());
        }
        PageHelper.startPage(trainSeatQueryReq.getPage(), trainSeatQueryReq.getSize());
        List<TrainSeat> trainSeats = trainSeatMapper.selectByExample(trainSeatExample);

        PageInfo<TrainSeat> pageInfo = new PageInfo<>(trainSeats);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<TrainSeatQueryResp> trainSeatQueryResps = BeanUtil.copyToList(trainSeats, TrainSeatQueryResp.class);
        PageResp<TrainSeatQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setData(trainSeatQueryResps);

        return pageResp;
    }

    public void delete(Long id) {
        trainSeatMapper.deleteByPrimaryKey(id);
    }

    // 生成座位
    @Transactional
    public void genTrainSeat(String trainCode) {
        DateTime now = DateTime.now();
        //清空座位
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        trainSeatMapper.deleteByExample(trainSeatExample);
        //查找当前车次所有车厢
        List<TrainCarriage> trainCarriages = trainCarriageService.selectByTrainCode(trainCode);
        // 创建总座位集合
        List<TrainSeat> trainSeats = new ArrayList<>();
        //循环生成每个车厢的座位
        for (TrainCarriage trainCarriage : trainCarriages) {
            //拿到车厢行数和座位类型
            Integer rowCount = trainCarriage.getRowCount();
            String seatType = trainCarriage.getSeatType();
            int seatIndex = 1;
            //根据座位类型，拿到列数
            List<SeatColEnum> colsByType = SeatColEnum.getColsByType(seatType);
            //遍历行数
            for (int row = 1; row <= rowCount; row++) {
                //循环列数
                for (SeatColEnum seatColEnum : colsByType) {
                    //构造座位数据，并汇总
                    TrainSeat trainSeat = new TrainSeat();
                    trainSeat.setId(SnowUtil.getSnowflakeNextId());
                    trainSeat.setTrainCode(trainCode);
                    trainSeat.setCarriageIndex(trainCarriage.getIndex());
                    trainSeat.setRow(StrUtil.fillBefore(String.valueOf(row), '0', 2));
                    trainSeat.setCol(seatColEnum.getKey());
                    trainSeat.setSeatType(seatType);
                    trainSeat.setCarriageSeatIndex(seatIndex++);
                    trainSeat.setCreateTime(now);
                    trainSeat.setUpdateTime(now);
                    trainSeats.add(trainSeat);
                }
            }
        }
        // 保存到数据库中
        int i = trainSeatMapper.insertBatch(trainSeats);
        // 打印日志
        LOG.info("生成座位成功，车次：{}，数量：{}", trainCode, i);
    }

    public List<TrainSeat> selectByTrainCode(String trainCode) {
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        return trainSeatMapper.selectByExample(trainSeatExample);
    }
}

package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.*;
import com.study.train.business.req.DailyTrainStationCarriageQueryReq;
import com.study.train.business.req.DailyTrainStationCarriageSaveReq;
import com.study.train.business.enums.SeatColEnum;
import com.study.train.business.mapper.DailyTrainStationCarriageMapper;
import com.study.train.business.resp.DailyTrainStationCarriageQueryResp;
import com.study.train.common.resp.PageResp;
import com.study.train.common.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainStationCarriageService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationCarriageService.class);

    @Resource
    DailyTrainStationCarriageMapper dailyTrainStationCarriageMapper;

    @Resource
    TrainCarriageService trainCarriageService;

    public void save(DailyTrainStationCarriageSaveReq dailyTrainStationCarriageSaveReq) {
        DateTime now = DateTime.now();

        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(dailyTrainStationCarriageSaveReq.getSeatType());
        dailyTrainStationCarriageSaveReq.setColCount(seatColEnums.size());
        dailyTrainStationCarriageSaveReq.setSeatCount(dailyTrainStationCarriageSaveReq.getColCount() * dailyTrainStationCarriageSaveReq.getRowCount());

        DailyTrainStationCarriage dailyTrainStationCarriage = BeanUtil.copyProperties(dailyTrainStationCarriageSaveReq, DailyTrainStationCarriage.class);
        if (ObjectUtil.isNull(dailyTrainStationCarriage.getId())) {
            dailyTrainStationCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStationCarriage.setCreateTime(now);
            dailyTrainStationCarriage.setUpdateTime(now);
            dailyTrainStationCarriageMapper.insert(dailyTrainStationCarriage);
        } else {
            dailyTrainStationCarriage.setUpdateTime(now);
            dailyTrainStationCarriageMapper.updateByPrimaryKey(dailyTrainStationCarriage);
        }

    }

    public PageResp<DailyTrainStationCarriageQueryResp> queryList(DailyTrainStationCarriageQueryReq dailyTrainStationCarriageQueryReq) {
        DailyTrainStationCarriageExample dailyTrainStationCarriageExample = new DailyTrainStationCarriageExample();
        dailyTrainStationCarriageExample.setOrderByClause("date desc, train_code asc, `index` asc");
        DailyTrainStationCarriageExample.Criteria criteria = dailyTrainStationCarriageExample.createCriteria();
        if (ObjectUtil.isNotNull(dailyTrainStationCarriageQueryReq.getDate())) {
            criteria.andDateEqualTo(dailyTrainStationCarriageQueryReq.getDate());
        }

        if (ObjectUtil.isNotEmpty(dailyTrainStationCarriageQueryReq.getTrainCode())) {
            criteria.andTrainCodeEqualTo(dailyTrainStationCarriageQueryReq.getTrainCode());
        }
        PageHelper.startPage(dailyTrainStationCarriageQueryReq.getPage(), dailyTrainStationCarriageQueryReq.getSize());
        List<DailyTrainStationCarriage> dailyTrainStationCarriages = dailyTrainStationCarriageMapper.selectByExample(dailyTrainStationCarriageExample);

        PageInfo<DailyTrainStationCarriage> pageInfo = new PageInfo<>(dailyTrainStationCarriages);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<DailyTrainStationCarriageQueryResp> dailyTrainStationCarriageQueryResps = BeanUtil.copyToList(dailyTrainStationCarriages, DailyTrainStationCarriageQueryResp.class);
        PageResp<DailyTrainStationCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setData(dailyTrainStationCarriageQueryResps);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainStationCarriageMapper.deleteByPrimaryKey(id);
    }

    public void genDaily(Date date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的车站数据开始", DateTime.of(date).toString("yyyy-MM-dd"), trainCode);
        //删除该车次车站所有每日数据
        DailyTrainStationCarriageExample dailyTrainStationCarriageExample = new DailyTrainStationCarriageExample();
        dailyTrainStationCarriageExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        dailyTrainStationCarriageMapper.deleteByExample(dailyTrainStationCarriageExample);
        //查出某车次车站所有信息

        List<TrainCarriage> trainCarriages = trainCarriageService.selectByTrainCode(trainCode);

        if (CollUtil.isEmpty(trainCarriages)) {
            LOG.info("该车次没有车站基础信息，生成该车站车站信息结束");
            return;
        }
        for (TrainCarriage trainStationCarriage : trainCarriages) {
            //生成该车次每日数据
            Date now = DateTime.now();
            DailyTrainStationCarriage dailyTrainStationCarriage = BeanUtil.copyProperties(trainStationCarriage, DailyTrainStationCarriage.class);
            dailyTrainStationCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStationCarriage.setCreateTime(now);
            dailyTrainStationCarriage.setUpdateTime(now);
            dailyTrainStationCarriage.setDate(date);
            dailyTrainStationCarriageMapper.insert(dailyTrainStationCarriage);
        }
        LOG.info("生成日期【{}】车次【{}】的车站数据结束", DateTime.of(date).toString("yyyy-MM-dd"), trainCode);
    }

    public List<DailyTrainStationCarriage> selectBySeatType(Date date, String trainCode, String seatType) {
        DailyTrainStationCarriageExample dailyTrainStationCarriageExample = new DailyTrainStationCarriageExample();
        dailyTrainStationCarriageExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode).andSeatTypeEqualTo(seatType);
        return dailyTrainStationCarriageMapper.selectByExample(dailyTrainStationCarriageExample);
    }
}

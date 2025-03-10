package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.DailyTrainStation;
import com.study.train.business.domain.DailyTrainStationExample;
import com.study.train.business.domain.TrainStation;
import com.study.train.business.mapper.DailyTrainStationMapper;
import com.study.train.business.req.DailyTrainStationQueryReq;
import com.study.train.business.req.DailyTrainStationSaveReq;
import com.study.train.business.resp.DailyTrainStationQueryResp;
import com.study.train.common.resp.PageResp;
import com.study.train.common.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainStationService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationService.class);

    @Resource
    DailyTrainStationMapper dailyTrainStationMapper;

    @Resource
    TrainStationService trainStationService;

    public void save(DailyTrainStationSaveReq dailyTrainStationSaveReq) {
        DateTime now = new DateTime();
        DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(dailyTrainStationSaveReq, DailyTrainStation.class);
        if (ObjectUtil.isNull(dailyTrainStation.getId())) {
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.insert(dailyTrainStation);
        } else {
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStationMapper.updateByPrimaryKey(dailyTrainStation);
        }

    }

    public PageResp<DailyTrainStationQueryResp> queryList(DailyTrainStationQueryReq dailyTrainStationQueryReq) {
        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        dailyTrainStationExample.setOrderByClause("date desc, train_code asc, `index` asc");
        DailyTrainStationExample.Criteria criteria = dailyTrainStationExample.createCriteria();
        if (ObjectUtil.isNotNull(dailyTrainStationQueryReq.getDate())) {
            criteria.andDateEqualTo(dailyTrainStationQueryReq.getDate());
        }

        if (ObjectUtil.isNotEmpty(dailyTrainStationQueryReq.getTrainCode())) {
            criteria.andTrainCodeEqualTo(dailyTrainStationQueryReq.getTrainCode());
        }
        PageHelper.startPage(dailyTrainStationQueryReq.getPage(), dailyTrainStationQueryReq.getSize());
        List<DailyTrainStation> dailyTrainStations = dailyTrainStationMapper.selectByExample(dailyTrainStationExample);

        PageInfo<DailyTrainStation> pageInfo = new PageInfo<>(dailyTrainStations);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<DailyTrainStationQueryResp> dailyTrainStationQueryResps = BeanUtil.copyToList(dailyTrainStations, DailyTrainStationQueryResp.class);
        PageResp<DailyTrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setData(dailyTrainStationQueryResps);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainStationMapper.deleteByPrimaryKey(id);
    }


    public void genDaily(Date date, String trainCode) {
        LOG.info("生成日期【{}】车次【{}】的车站数据开始", date, trainCode);
        //删除该车次车站所有每日数据
        DailyTrainStationExample dailyTrainStationExample = new DailyTrainStationExample();
        dailyTrainStationExample.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        dailyTrainStationMapper.deleteByExample(dailyTrainStationExample);
        //查出某车次车站所有信息

        List<TrainStation> trainStations = trainStationService.selectByTrainCode(trainCode);

        if (CollUtil.isEmpty(trainStations)) {
            LOG.info("该车次没有车站基础信息");
        }
        for (TrainStation trainStation : trainStations) {
            //生成该车次每日数据
            Date now = DateTime.now();
            DailyTrainStation dailyTrainStation = BeanUtil.copyProperties(trainStation, DailyTrainStation.class);
            dailyTrainStation.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStation.setCreateTime(now);
            dailyTrainStation.setUpdateTime(now);
            dailyTrainStation.setDate(date);
            dailyTrainStationMapper.insert(dailyTrainStation);
        }
        LOG.info("生成日期【{}】车次【{}】的车站数据结束", date, trainCode);
    }

    /**
     * 按车次查询全部车站
     */
    public long countByTrainCode(Date date, String trainCode) {
        DailyTrainStationExample example = new DailyTrainStationExample();
        example.createCriteria().andDateEqualTo(date).andTrainCodeEqualTo(trainCode);
        long stationCount = dailyTrainStationMapper.countByExample(example);
        return stationCount;
    }
}

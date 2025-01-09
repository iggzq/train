package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.TrainStation;
import com.study.train.business.domain.TrainStationExample;
import com.study.train.business.req.TrainStationQueryReq;
import com.study.train.business.req.TrainStationSaveReq;
import com.study.train.business.mapper.TrainStationMapper;
import com.study.train.business.resp.TrainStationQueryResp;
import com.study.train.common.exception.BusinessException;
import com.study.train.common.exception.BusinessExceptionEnum;
import com.study.train.common.resp.PageResp;
import com.study.train.common.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainStationService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainStationService.class);

    @Resource
    TrainStationMapper trainStationMapper;

    public void save(TrainStationSaveReq trainStationSaveReq) {
        DateTime now = new DateTime();
        TrainStation trainStation = BeanUtil.copyProperties(trainStationSaveReq, TrainStation.class);
        if (ObjectUtil.isNull(trainStation.getId())) {
            TrainStation byUnique = selectByUnique(trainStationSaveReq.getTrainCode(), trainStationSaveReq.getIndex());
            if (ObjectUtil.isNotNull(byUnique)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_STATION_INDEX_UNIQUE_ERROR);
            }
            trainStation.setId(SnowUtil.getSnowflakeNextId());
            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStationMapper.insert(trainStation);
        } else {
            trainStation.setUpdateTime(now);
            trainStationMapper.updateByPrimaryKey(trainStation);
        }

    }

    private TrainStation selectByUnique(String trainCode, Integer index) {
        TrainStationExample trainStationExample = new TrainStationExample();
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode).andIndexEqualTo(index);
        List<TrainStation> trainStations = trainStationMapper.selectByExample(trainStationExample);
        if (CollUtil.isNotEmpty(trainStations)) {
            return trainStations.get(0);
        } else {
            return null;
        }
    }

    private TrainStation selectByUnique(String trainCode, String name) {
        TrainStationExample trainStationExample = new TrainStationExample();
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode).andNameEqualTo(name);
        List<TrainStation> trainStations = trainStationMapper.selectByExample(trainStationExample);
        if (CollUtil.isNotEmpty(trainStations)) {
            return trainStations.get(0);
        } else {
            return null;
        }
    }

    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryReq trainStationQueryReq) {
        TrainStationExample trainStationExample = new TrainStationExample();
        trainStationExample.setOrderByClause("`train_code` desc,`index` asc");
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
        if (ObjectUtil.isNotEmpty(trainStationQueryReq.getTrainCode())) {
            criteria.andTrainCodeEqualTo(trainStationQueryReq.getTrainCode());
        }
        PageHelper.startPage(trainStationQueryReq.getPage(), trainStationQueryReq.getSize());
        List<TrainStation> trainStations = trainStationMapper.selectByExample(trainStationExample);

        PageInfo<TrainStation> pageInfo = new PageInfo<>(trainStations);


        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<TrainStationQueryResp> trainStationQueryResps = BeanUtil.copyToList(trainStations, TrainStationQueryResp.class);
        PageResp<TrainStationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setData(trainStationQueryResps);

        return pageResp;
    }

    public void delete(Long id) {
        trainStationMapper.deleteByPrimaryKey(id);
    }

    public List<TrainStation> selectByTrainCode(String trainCode) {
        TrainStationExample trainStationExample = new TrainStationExample();
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        return trainStationMapper.selectByExample(trainStationExample);
    }
}

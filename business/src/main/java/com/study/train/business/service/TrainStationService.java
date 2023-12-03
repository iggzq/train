package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.common.util.SnowUtil;
import com.study.train.business.domain.TrainStation;
import com.study.train.business.domain.TrainStationExample;
import com.study.train.common.resp.PageResp;
import com.study.train.business.dto.TrainStationQueryDTO;
import com.study.train.business.dto.TrainStationSaveDTO;
import com.study.train.business.mapper.TrainStationMapper;
import com.study.train.business.resp.TrainStationQueryResp;
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

    public void save(TrainStationSaveDTO trainStationSaveDTO) {
        DateTime now = new DateTime();
        TrainStation trainStation = BeanUtil.copyProperties(trainStationSaveDTO, TrainStation.class);
        if (ObjectUtil.isNull(trainStation.getId())) {
            trainStation.setId(SnowUtil.getSnowflakeNextId());
            trainStation.setCreateTime(now);
            trainStation.setUpdateTime(now);
            trainStationMapper.insert(trainStation);
        }else {
            trainStation.setUpdateTime(now);
            trainStationMapper.updateByPrimaryKey(trainStation);
        }

    }

    public PageResp<TrainStationQueryResp> queryList(TrainStationQueryDTO trainStationQueryDTO) {
        TrainStationExample trainStationExample = new TrainStationExample();
        TrainStationExample.Criteria criteria = trainStationExample.createCriteria();
        PageHelper.startPage(trainStationQueryDTO.getPage(), trainStationQueryDTO.getSize());
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

    public void delete(Long id){
        trainStationMapper.deleteByPrimaryKey(id);
    }
}

package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.common.util.SnowUtil;
import com.study.train.business.domain.TrainSeat;
import com.study.train.business.domain.TrainSeatExample;
import com.study.train.common.resp.PageResp;
import com.study.train.business.dto.TrainSeatQueryDTO;
import com.study.train.business.dto.TrainSeatSaveDTO;
import com.study.train.business.mapper.TrainSeatMapper;
import com.study.train.business.resp.TrainSeatQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainSeatService.class);

    @Resource
    TrainSeatMapper trainSeatMapper;

    public void save(TrainSeatSaveDTO trainSeatSaveDTO) {
        DateTime now = new DateTime();
        TrainSeat trainSeat = BeanUtil.copyProperties(trainSeatSaveDTO, TrainSeat.class);
        if (ObjectUtil.isNull(trainSeat.getId())) {
            trainSeat.setId(SnowUtil.getSnowflakeNextId());
            trainSeat.setCreateTime(now);
            trainSeat.setUpdateTime(now);
            trainSeatMapper.insert(trainSeat);
        }else {
            trainSeat.setUpdateTime(now);
            trainSeatMapper.updateByPrimaryKey(trainSeat);
        }

    }

    public PageResp<TrainSeatQueryResp> queryList(TrainSeatQueryDTO trainSeatQueryDTO) {
        TrainSeatExample trainSeatExample = new TrainSeatExample();
        TrainSeatExample.Criteria criteria = trainSeatExample.createCriteria();
        if (ObjectUtil.isNotEmpty(trainSeatQueryDTO.getTrainCode())) {
            criteria.andTrainCodeEqualTo(trainSeatQueryDTO.getTrainCode());
        }
        PageHelper.startPage(trainSeatQueryDTO.getPage(), trainSeatQueryDTO.getSize());
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

    public void delete(Long id){
        trainSeatMapper.deleteByPrimaryKey(id);
    }
}

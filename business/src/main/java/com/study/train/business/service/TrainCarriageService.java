package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.TrainCarriage;
import com.study.train.business.domain.TrainCarriageExample;
import com.study.train.business.req.TrainCarriageQueryReq;
import com.study.train.business.req.TrainCarriageSaveReq;
import com.study.train.business.enums.SeatColEnum;
import com.study.train.business.mapper.TrainCarriageMapper;
import com.study.train.business.resp.TrainCarriageQueryResp;
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
public class TrainCarriageService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainCarriageService.class);

    @Resource
    TrainCarriageMapper trainCarriageMapper;

    public void save(TrainCarriageSaveReq trainCarriageSaveReq) {
        DateTime now = DateTime.now();
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(trainCarriageSaveReq.getSeatType());
        trainCarriageSaveReq.setColCount(seatColEnums.size());
        trainCarriageSaveReq.setSeatCount(trainCarriageSaveReq.getColCount() * trainCarriageSaveReq.getRowCount());
        TrainCarriage trainCarriage = BeanUtil.copyProperties(trainCarriageSaveReq, TrainCarriage.class);
        if (ObjectUtil.isNull(trainCarriage.getId())) {
            TrainCarriage byUnique = selectByUnique(trainCarriageSaveReq.getTrainCode(), trainCarriageSaveReq.getIndex());
            if (ObjectUtil.isNotNull(byUnique)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CARRIAGE_INDEX_UNIQUE_ERROR);
            }
            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.insert(trainCarriage);
        } else {
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.updateByPrimaryKey(trainCarriage);
        }

    }

    private TrainCarriage selectByUnique(String trainCode, Integer index) {
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode).andIndexEqualTo(index);
        List<TrainCarriage> trainCarriages = trainCarriageMapper.selectByExample(trainCarriageExample);
        if (CollUtil.isNotEmpty(trainCarriages)) {
            return trainCarriages.get(0);
        } else {
            return null;
        }
    }

    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryReq trainCarriageQueryReq) {
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
        if (ObjectUtil.isNotEmpty(trainCarriageQueryReq.getTrainCode())) {
            criteria.andTrainCodeEqualTo(trainCarriageQueryReq.getTrainCode());
        }
        PageHelper.startPage(trainCarriageQueryReq.getPage(), trainCarriageQueryReq.getSize());
        List<TrainCarriage> trainCarriages = trainCarriageMapper.selectByExample(trainCarriageExample);

        PageInfo<TrainCarriage> pageInfo = new PageInfo<>(trainCarriages);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<TrainCarriageQueryResp> trainCarriageQueryResps = BeanUtil.copyToList(trainCarriages, TrainCarriageQueryResp.class);
        PageResp<TrainCarriageQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setData(trainCarriageQueryResps);

        return pageResp;
    }

    public void delete(Long id) {
        trainCarriageMapper.deleteByPrimaryKey(id);
    }

    public List<TrainCarriage> selectByTrainCode(String trainCode) {
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
        criteria.andTrainCodeEqualTo(trainCode);
        return trainCarriageMapper.selectByExample(trainCarriageExample);
    }


}

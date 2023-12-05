package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.TrainCarriage;
import com.study.train.business.domain.TrainCarriageExample;
import com.study.train.business.dto.TrainCarriageQueryDTO;
import com.study.train.business.dto.TrainCarriageSaveDTO;
import com.study.train.business.enums.SeatColEnum;
import com.study.train.business.mapper.TrainCarriageMapper;
import com.study.train.business.resp.TrainCarriageQueryResp;
import com.study.train.common.exception.BusinessException;
import com.study.train.common.exception.BusinessExceptionEnum;
import com.study.train.common.resp.PageResp;
import com.study.train.common.util.SnowUtil;
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

    public void save(TrainCarriageSaveDTO trainCarriageSaveDTO) {
        DateTime now = DateTime.now();
        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(trainCarriageSaveDTO.getSeatType());
        trainCarriageSaveDTO.setColCount(seatColEnums.size());
        trainCarriageSaveDTO.setSeatCount(trainCarriageSaveDTO.getColCount() * trainCarriageSaveDTO.getRowCount());
        TrainCarriage trainCarriage = BeanUtil.copyProperties(trainCarriageSaveDTO, TrainCarriage.class);
        if (ObjectUtil.isNull(trainCarriage.getId())) {
            TrainCarriage byUnique = selectByUnique(trainCarriageSaveDTO.getTrainCode(), trainCarriageSaveDTO.getIndex());
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

    public PageResp<TrainCarriageQueryResp> queryList(TrainCarriageQueryDTO trainCarriageQueryDTO) {
        TrainCarriageExample trainCarriageExample = new TrainCarriageExample();
        TrainCarriageExample.Criteria criteria = trainCarriageExample.createCriteria();
        if (ObjectUtil.isNotEmpty(trainCarriageQueryDTO.getTrainCode())) {
            criteria.andTrainCodeEqualTo(trainCarriageQueryDTO.getTrainCode());
        }
        PageHelper.startPage(trainCarriageQueryDTO.getPage(), trainCarriageQueryDTO.getSize());
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

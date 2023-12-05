package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.common.util.SnowUtil;
import com.study.train.business.domain.TrainCarriage;
import com.study.train.business.domain.TrainCarriageExample;
import com.study.train.common.resp.PageResp;
import com.study.train.business.dto.TrainCarriageQueryDTO;
import com.study.train.business.dto.TrainCarriageSaveDTO;
import com.study.train.business.mapper.TrainCarriageMapper;
import com.study.train.business.resp.TrainCarriageQueryResp;
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
        DateTime now = new DateTime();
        TrainCarriage trainCarriage = BeanUtil.copyProperties(trainCarriageSaveDTO, TrainCarriage.class);
        if (ObjectUtil.isNull(trainCarriage.getId())) {
            trainCarriage.setId(SnowUtil.getSnowflakeNextId());
            trainCarriage.setCreateTime(now);
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.insert(trainCarriage);
        }else {
            trainCarriage.setUpdateTime(now);
            trainCarriageMapper.updateByPrimaryKey(trainCarriage);
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

    public void delete(Long id){
        trainCarriageMapper.deleteByPrimaryKey(id);
    }
}

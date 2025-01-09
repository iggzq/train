package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.Train;
import com.study.train.business.domain.TrainExample;
import com.study.train.business.req.TrainQueryReq;
import com.study.train.business.req.TrainSaveReq;
import com.study.train.business.mapper.TrainMapper;
import com.study.train.business.resp.TrainQueryResp;
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
public class TrainService {

    private static final Logger LOG = LoggerFactory.getLogger(TrainService.class);

    @Resource
    TrainMapper trainMapper;

    public void save(TrainSaveReq trainSaveReq) {
        DateTime now = new DateTime();
        Train train = BeanUtil.copyProperties(trainSaveReq, Train.class);
        if (ObjectUtil.isNull(train.getId())) {

            Train byUnique = selectByUnique(trainSaveReq.getCode());
            if(ObjectUtil.isNotNull(byUnique)){
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_TRAIN_CODE_UNIQUE_ERROR);
            }
            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);
        }else {
            train.setUpdateTime(now);
            trainMapper.updateByPrimaryKey(train);
        }

    }

    private Train selectByUnique(String trainCode   ) {
        TrainExample trainExample = new TrainExample();
        TrainExample.Criteria criteria = trainExample.createCriteria();
        criteria.andCodeEqualTo(trainCode);
        List<Train> trains = trainMapper.selectByExample(trainExample);
        if (CollUtil.isNotEmpty(trains)) {
            return trains.get(0);
        } else {
            return null;
        }
    }

    public PageResp<TrainQueryResp> queryList(TrainQueryReq trainQueryReq) {
        TrainExample trainExample = new TrainExample();
        TrainExample.Criteria criteria = trainExample.createCriteria();
        PageHelper.startPage(trainQueryReq.getPage(), trainQueryReq.getSize());
        List<Train> trains = trainMapper.selectByExample(trainExample);

        PageInfo<Train> pageInfo = new PageInfo<>(trains);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<TrainQueryResp> trainQueryResps = BeanUtil.copyToList(trains, TrainQueryResp.class);
        PageResp<TrainQueryResp> pageResp = new PageResp<>();
            pageResp.setTotal(pageInfo.getTotal());
            pageResp.setData(trainQueryResps);

        return pageResp;
    }

    public List<TrainQueryResp> queryAll() {
        List<Train> trains = selectAll();
        return BeanUtil.copyToList(trains,TrainQueryResp.class);
    }

    public List<Train> selectAll() {
        TrainExample trainExample = new TrainExample();
        return trainMapper.selectByExample(trainExample);
    }

    public void delete(Long id){
        trainMapper.deleteByPrimaryKey(id);
    }
}

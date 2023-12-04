package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.Train;
import com.study.train.business.domain.TrainExample;
import com.study.train.business.dto.TrainQueryDTO;
import com.study.train.business.dto.TrainSaveDTO;
import com.study.train.business.mapper.TrainMapper;
import com.study.train.business.resp.TrainQueryResp;
import com.study.train.common.resp.PageResp;
import com.study.train.common.util.SnowUtil;
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

    public void save(TrainSaveDTO trainSaveDTO) {
        DateTime now = new DateTime();
        Train train = BeanUtil.copyProperties(trainSaveDTO, Train.class);
        if (ObjectUtil.isNull(train.getId())) {
            train.setId(SnowUtil.getSnowflakeNextId());
            train.setCreateTime(now);
            train.setUpdateTime(now);
            trainMapper.insert(train);
        }else {
            train.setUpdateTime(now);
            trainMapper.updateByPrimaryKey(train);
        }

    }

    public PageResp<TrainQueryResp> queryList(TrainQueryDTO trainQueryDTO) {
        TrainExample trainExample = new TrainExample();
        TrainExample.Criteria criteria = trainExample.createCriteria();
        PageHelper.startPage(trainQueryDTO.getPage(), trainQueryDTO.getSize());
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
        TrainExample trainExample = new TrainExample();
        trainExample.setOrderByClause("key value");
        List<Train> trains = trainMapper.selectByExample(trainExample);
        return BeanUtil.copyToList(trains,TrainQueryResp.class);
    }

    public void delete(Long id){
        trainMapper.deleteByPrimaryKey(id);
    }
}

package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.util.DateUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.Train;
import com.study.train.common.util.SnowUtil;
import com.study.train.business.domain.DailyTrain;
import com.study.train.business.domain.DailyTrainExample;
import com.study.train.common.resp.PageResp;
import com.study.train.business.dto.DailyTrainQueryDTO;
import com.study.train.business.dto.DailyTrainSaveDTO;
import com.study.train.business.mapper.DailyTrainMapper;
import com.study.train.business.resp.DailyTrainQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainService.class);

    @Resource
    DailyTrainMapper dailyTrainMapper;

    @Resource
    TrainService trainService;

    public void save(DailyTrainSaveDTO dailyTrainSaveDTO) {
        DateTime now = new DateTime();
        DailyTrain dailyTrain = BeanUtil.copyProperties(dailyTrainSaveDTO, DailyTrain.class);
        if (ObjectUtil.isNull(dailyTrain.getId())) {
            dailyTrain.setId(SnowUtil.getSnowflakeNextId());
            dailyTrain.setCreateTime(now);
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.insert(dailyTrain);
        } else {
            dailyTrain.setUpdateTime(now);
            dailyTrainMapper.updateByPrimaryKey(dailyTrain);
        }

    }

    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryDTO dailyTrainQueryDTO) {
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.setOrderByClause("date desc");
        DailyTrainExample.Criteria criteria = dailyTrainExample.createCriteria();

        if (ObjectUtil.isNotNull(dailyTrainQueryDTO.getDate())) {
            criteria.andDateEqualTo(dailyTrainQueryDTO.getDate());
        }

        if (ObjectUtil.isNotEmpty(dailyTrainQueryDTO.getCode())) {
            criteria.andCodeEqualTo(dailyTrainQueryDTO.getCode());
        }
        PageHelper.startPage(dailyTrainQueryDTO.getPage(), dailyTrainQueryDTO.getSize());
        List<DailyTrain> dailyTrains = dailyTrainMapper.selectByExample(dailyTrainExample);


        PageInfo<DailyTrain> pageInfo = new PageInfo<>(dailyTrains);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<DailyTrainQueryResp> dailyTrainQueryResps = BeanUtil.copyToList(dailyTrains, DailyTrainQueryResp.class);
        PageResp<DailyTrainQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setData(dailyTrainQueryResps);

        return pageResp;
    }

    public void delete(Long id) {
        dailyTrainMapper.deleteByPrimaryKey(id);
    }


    public void genDaily(Date date) {
        List<Train> trains = trainService.selectAll();
        if(CollUtil.isEmpty(trains)){
            LOG.info("没有车次基础数据，任务结束");
            return;
        }
        for (Train train : trains) {
            genDailyTrain(date,train);
        }
    }

    public void genDailyTrain(Date date,Train train){
        //删除该车次每日数据
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.createCriteria().andDateEqualTo(date).andCodeEqualTo(train.getCode());
        dailyTrainMapper.deleteByExample(dailyTrainExample);
        //生成该车次每日数据
        Date now = DateTime.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(train, DailyTrain.class);
        dailyTrain.setId(SnowUtil.getSnowflakeNextId());
        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);
        dailyTrain.setDate(date);
        dailyTrainMapper.insert(dailyTrain);
    }
}

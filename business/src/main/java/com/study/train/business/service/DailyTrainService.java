package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.DailyTrain;
import com.study.train.business.domain.DailyTrainExample;
import com.study.train.business.domain.Train;
import com.study.train.business.mapper.DailyTrainMapper;
import com.study.train.business.req.DailyTrainQueryReq;
import com.study.train.business.req.DailyTrainSaveReq;
import com.study.train.business.resp.DailyTrainQueryResp;
import com.study.train.common.resp.PageResp;
import com.study.train.common.utils.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class DailyTrainService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainService.class);

    @Resource
    DailyTrainMapper dailyTrainMapper;

    @Resource
    TrainService trainService;

    @Resource
    DailyTrainStationService dailyTrainStationService;

    @Resource
    DailyTrainStationCarriageService dailyTrainStationCarriageService;

    @Resource
    DailyTrainStationSeatService dailyTrainStationSeatService;

    @Resource
    DailyTrainTicketService dailyTrainTicketService;

    @Resource
    private SkTokenService skTokenService;

    public void save(DailyTrainSaveReq dailyTrainSaveReq) {
        DateTime now = new DateTime();
        DailyTrain dailyTrain = BeanUtil.copyProperties(dailyTrainSaveReq, DailyTrain.class);
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

    @Cacheable(value = "DailyTrainService.queryList")
    public PageResp<DailyTrainQueryResp> queryList(DailyTrainQueryReq dailyTrainQueryReq) {
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.setOrderByClause("date desc");
        DailyTrainExample.Criteria criteria = dailyTrainExample.createCriteria();

        if (ObjectUtil.isNotNull(dailyTrainQueryReq.getDate())) {
            criteria.andDateEqualTo(dailyTrainQueryReq.getDate());
        }

        if (ObjectUtil.isNotEmpty(dailyTrainQueryReq.getCode())) {
            criteria.andCodeEqualTo(dailyTrainQueryReq.getCode());
        }
        PageHelper.startPage(dailyTrainQueryReq.getPage(), dailyTrainQueryReq.getSize());
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


    @CacheEvict(value = "DailyTrainService.queryList", allEntries = true)
    @Transactional
    public void genDaily(Date date) {
        List<Train> trainList = trainService.selectAll();
        if (CollUtil.isEmpty(trainList)) {
            LOG.info("没有车次基础数据，任务结束");
            return;
        }

        for (Train train : trainList) {
            genDailyTrain(date, train);
        }
    }

    public void genDailyTrain(Date date, Train train) {
        LOG.info("生成日期【{}】车次【{}】的信息开始", DateTime.of(date).toString("yyyy-MM-dd"), train.getCode());
        //删除该车次每日数据
        DailyTrainExample dailyTrainExample = new DailyTrainExample();
        dailyTrainExample.createCriteria().andDateEqualTo(date).andCodeEqualTo(train.getCode());
        dailyTrainMapper.deleteByExample(dailyTrainExample);
        //生成该车次十四天后的数据
        Date now = DateTime.now();
        DailyTrain dailyTrain = BeanUtil.copyProperties(train, DailyTrain.class);
        dailyTrain.setId(SnowUtil.getSnowflakeNextId());
        dailyTrain.setCreateTime(now);
        dailyTrain.setUpdateTime(now);
        dailyTrain.setDate(date);
        dailyTrainMapper.insert(dailyTrain);
        //生成该车次车站信息
        dailyTrainStationService.genDaily(date, train.getCode());
        //生成该车次车厢数据
        dailyTrainStationCarriageService.genDaily(date, train.getCode());
        //生成每日车次座位信息
        dailyTrainStationSeatService.genDaily(date, train.getCode());
        //生成票数信息
        dailyTrainTicketService.genDaily(dailyTrain, date, train.getCode());
        // 生成令牌余量数据
        skTokenService.genDaily(date, train.getCode());
        LOG.info("生成日期【{}】车次【{}】的信息结束", DateTime.of(date).toString("yyyy-MM-dd"), train.getCode());
    }
}

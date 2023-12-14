package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.common.util.SnowUtil;
import com.study.train.business.domain.DailyTrainStationSeat;
import com.study.train.business.domain.DailyTrainStationSeatExample;
import com.study.train.common.resp.PageResp;
import com.study.train.business.dto.DailyTrainStationSeatQueryDTO;
import com.study.train.business.dto.DailyTrainStationSeatSaveDTO;
import com.study.train.business.mapper.DailyTrainStationSeatMapper;
import com.study.train.business.resp.DailyTrainStationSeatQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyTrainStationSeatService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationSeatService.class);

    @Resource
    DailyTrainStationSeatMapper dailyTrainStationSeatMapper;

    public void save(DailyTrainStationSeatSaveDTO dailyTrainStationSeatSaveDTO) {
        DateTime now = new DateTime();
        DailyTrainStationSeat dailyTrainStationSeat = BeanUtil.copyProperties(dailyTrainStationSeatSaveDTO, DailyTrainStationSeat.class);
        if (ObjectUtil.isNull(dailyTrainStationSeat.getId())) {
            dailyTrainStationSeat.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStationSeat.setCreateTime(now);
            dailyTrainStationSeat.setUpdateTime(now);
            dailyTrainStationSeatMapper.insert(dailyTrainStationSeat);
        }else {
            dailyTrainStationSeat.setUpdateTime(now);
            dailyTrainStationSeatMapper.updateByPrimaryKey(dailyTrainStationSeat);
        }

    }

    public PageResp<DailyTrainStationSeatQueryResp> queryList(DailyTrainStationSeatQueryDTO dailyTrainStationSeatQueryDTO) {
        DailyTrainStationSeatExample dailyTrainStationSeatExample = new DailyTrainStationSeatExample();
        DailyTrainStationSeatExample.Criteria criteria = dailyTrainStationSeatExample.createCriteria();
        PageHelper.startPage(dailyTrainStationSeatQueryDTO.getPage(), dailyTrainStationSeatQueryDTO.getSize());
        List<DailyTrainStationSeat> dailyTrainStationSeats = dailyTrainStationSeatMapper.selectByExample(dailyTrainStationSeatExample);

        PageInfo<DailyTrainStationSeat> pageInfo = new PageInfo<>(dailyTrainStationSeats);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<DailyTrainStationSeatQueryResp> dailyTrainStationSeatQueryResps = BeanUtil.copyToList(dailyTrainStationSeats, DailyTrainStationSeatQueryResp.class);
        PageResp<DailyTrainStationSeatQueryResp> pageResp = new PageResp<>();
            pageResp.setTotal(pageInfo.getTotal());
            pageResp.setData(dailyTrainStationSeatQueryResps);

        return pageResp;
    }

    public void delete(Long id){
        dailyTrainStationSeatMapper.deleteByPrimaryKey(id);
    }
}

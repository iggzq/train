package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.DailyTrainStationCarriage;
import com.study.train.business.domain.DailyTrainStationCarriageExample;
import com.study.train.business.dto.DailyTrainStationCarriageQueryDTO;
import com.study.train.business.dto.DailyTrainStationCarriageSaveDTO;
import com.study.train.business.enums.SeatColEnum;
import com.study.train.business.mapper.DailyTrainStationCarriageMapper;
import com.study.train.business.resp.DailyTrainStationCarriageQueryResp;
import com.study.train.common.resp.PageResp;
import com.study.train.common.util.SnowUtil;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DailyTrainStationCarriageService {

    private static final Logger LOG = LoggerFactory.getLogger(DailyTrainStationCarriageService.class);

    @Resource
    DailyTrainStationCarriageMapper dailyTrainStationCarriageMapper;

    public void save(DailyTrainStationCarriageSaveDTO dailyTrainStationCarriageSaveDTO) {
        DateTime now = DateTime.now();

        List<SeatColEnum> seatColEnums = SeatColEnum.getColsByType(dailyTrainStationCarriageSaveDTO.getSeatType());
        dailyTrainStationCarriageSaveDTO.setColCount(seatColEnums.size());
        dailyTrainStationCarriageSaveDTO.setSeatCount(dailyTrainStationCarriageSaveDTO.getColCount() * dailyTrainStationCarriageSaveDTO.getRowCount());

        DailyTrainStationCarriage dailyTrainStationCarriage = BeanUtil.copyProperties(dailyTrainStationCarriageSaveDTO, DailyTrainStationCarriage.class);
        if (ObjectUtil.isNull(dailyTrainStationCarriage.getId())) {
            dailyTrainStationCarriage.setId(SnowUtil.getSnowflakeNextId());
            dailyTrainStationCarriage.setCreateTime(now);
            dailyTrainStationCarriage.setUpdateTime(now);
            dailyTrainStationCarriageMapper.insert(dailyTrainStationCarriage);
        }else {
            dailyTrainStationCarriage.setUpdateTime(now);
            dailyTrainStationCarriageMapper.updateByPrimaryKey(dailyTrainStationCarriage);
        }

    }

    public PageResp<DailyTrainStationCarriageQueryResp> queryList(DailyTrainStationCarriageQueryDTO dailyTrainStationCarriageQueryDTO) {
        DailyTrainStationCarriageExample dailyTrainStationCarriageExample = new DailyTrainStationCarriageExample();
        dailyTrainStationCarriageExample.setOrderByClause("date desc");
        DailyTrainStationCarriageExample.Criteria criteria = dailyTrainStationCarriageExample.createCriteria();
        if (ObjectUtil.isNotNull(dailyTrainStationCarriageQueryDTO.getDate())) {
            criteria.andDateEqualTo(dailyTrainStationCarriageQueryDTO.getDate());
        }

        if (ObjectUtil.isNotEmpty(dailyTrainStationCarriageQueryDTO.getTrainCode())) {
            criteria.andTrainCodeEqualTo(dailyTrainStationCarriageQueryDTO.getTrainCode());
        }
        PageHelper.startPage(dailyTrainStationCarriageQueryDTO.getPage(), dailyTrainStationCarriageQueryDTO.getSize());
        List<DailyTrainStationCarriage> dailyTrainStationCarriages = dailyTrainStationCarriageMapper.selectByExample(dailyTrainStationCarriageExample);

        PageInfo<DailyTrainStationCarriage> pageInfo = new PageInfo<>(dailyTrainStationCarriages);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<DailyTrainStationCarriageQueryResp> dailyTrainStationCarriageQueryResps = BeanUtil.copyToList(dailyTrainStationCarriages, DailyTrainStationCarriageQueryResp.class);
        PageResp<DailyTrainStationCarriageQueryResp> pageResp = new PageResp<>();
            pageResp.setTotal(pageInfo.getTotal());
            pageResp.setData(dailyTrainStationCarriageQueryResps);

        return pageResp;
    }

    public void delete(Long id){
        dailyTrainStationCarriageMapper.deleteByPrimaryKey(id);
    }
}

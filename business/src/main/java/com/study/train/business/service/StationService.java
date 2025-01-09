package com.study.train.business.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.business.domain.Station;
import com.study.train.business.domain.StationExample;
import com.study.train.business.req.StationQueryReq;
import com.study.train.business.req.StationSaveReq;
import com.study.train.business.mapper.StationMapper;
import com.study.train.business.resp.StationQueryResp;
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
public class StationService {

    private static final Logger LOG = LoggerFactory.getLogger(StationService.class);

    @Resource
    StationMapper stationMapper;

    public void save(StationSaveReq stationSaveReq) {
        DateTime now = new DateTime();
        Station station = BeanUtil.copyProperties(stationSaveReq, Station.class);
        if (ObjectUtil.isNull(station.getId())) {
            //校验唯一键是否存在
            Station stationDB = selectByUnique(stationSaveReq.getName());
            if (ObjectUtil.isNotNull(stationDB)) {
                throw new BusinessException(BusinessExceptionEnum.BUSINESS_STATION_NAME_UNIQUE_ERROR);
            }

            station.setId(SnowUtil.getSnowflakeNextId());
            station.setCreateTime(now);
            station.setUpdateTime(now);
            stationMapper.insert(station);
        } else {
            station.setUpdateTime(now);
            stationMapper.updateByPrimaryKey(station);
        }

    }

    private Station selectByUnique(String name) {
        StationExample stationExample = new StationExample();
        StationExample.Criteria criteria = stationExample.createCriteria();
        criteria.andNameEqualTo(name);
        List<Station> stations = stationMapper.selectByExample(stationExample);
        if (CollUtil.isNotEmpty(stations)) {
            return stations.get(0);
        } else {
            return null;
        }
    }

    public PageResp<StationQueryResp> queryList(StationQueryReq stationQueryReq) {
        StationExample stationExample = new StationExample();
        StationExample.Criteria criteria = stationExample.createCriteria();
        PageHelper.startPage(stationQueryReq.getPage(), stationQueryReq.getSize());
        List<Station> stations = stationMapper.selectByExample(stationExample);

        PageInfo<Station> pageInfo = new PageInfo<>(stations);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<StationQueryResp> stationQueryResps = BeanUtil.copyToList(stations, StationQueryResp.class);
        PageResp<StationQueryResp> pageResp = new PageResp<>();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setData(stationQueryResps);

        return pageResp;
    }

    public List<StationQueryResp> queryAll() {
        StationExample stationExample = new StationExample();
        List<Station> stations = stationMapper.selectByExample(stationExample);
        return BeanUtil.copyToList(stations, StationQueryResp.class);
    }

    public void delete(Long id) {
        stationMapper.deleteByPrimaryKey(id);
    }
}

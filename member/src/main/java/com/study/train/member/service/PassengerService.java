package com.study.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.util.SnowUtil;
import com.study.train.member.domain.Passenger;
import com.study.train.member.domain.PassengerExample;
import com.study.train.member.dto.PageDTO;
import com.study.train.member.dto.PassengerQueryDTO;
import com.study.train.member.dto.PassengerSaveDTO;
import com.study.train.member.mapper.PassengerMapper;
import com.study.train.member.resp.PassengerQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PassengerService {

    private static final Logger LOG = LoggerFactory.getLogger(PassengerService.class);

    @Resource
    PassengerMapper passengerMapper;

    public void save(PassengerSaveDTO passengerSaveDTO) {
        Date date = new Date();
        Passenger passenger = BeanUtil.copyProperties(passengerSaveDTO, Passenger.class);
        passenger.setMemberId(LoginMemberContext.getId());
        passenger.setId(SnowUtil.getSnowflakeNextId());
        passenger.setCreateTime(date);
        passenger.setUpdateTime(date);
        passengerMapper.insert(passenger);
    }

    public PageDTO<PassengerQueryResp> queryList(PassengerQueryDTO passengerQueryDTO) {
        PassengerExample passengerExample = new PassengerExample();
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        if (ObjectUtil.isNotNull(passengerQueryDTO.getMemberId())) {
            criteria.andMemberIdEqualTo(passengerQueryDTO.getMemberId());
        }
        PageHelper.startPage(passengerQueryDTO.getPage(), passengerQueryDTO.getSize());
        List<Passenger> passengers = passengerMapper.selectByExample(passengerExample);

        PageInfo<Passenger> pageInfo = new PageInfo<>(passengers);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<PassengerQueryResp> passengerQueryResps = BeanUtil.copyToList(passengers, PassengerQueryResp.class);
        PageDTO<PassengerQueryResp> pageDTO = new PageDTO<>();
        pageDTO.setTotal(pageInfo.getTotal());
        pageDTO.setData(passengerQueryResps);

        return pageDTO;
    }
}

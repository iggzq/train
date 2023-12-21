package com.study.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.util.SnowUtil;
import com.study.train.member.domain.Passenger;
import com.study.train.member.domain.PassengerExample;
import com.study.train.common.resp.PageResp;
import com.study.train.member.dto.PassengerQueryDTO;
import com.study.train.member.dto.PassengerSaveDTO;
import com.study.train.member.mapper.PassengerMapper;
import com.study.train.member.resp.PassengerQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PassengerService {

    private static final Logger LOG = LoggerFactory.getLogger(PassengerService.class);

    @Resource
    PassengerMapper passengerMapper;

    public void save(PassengerSaveDTO passengerSaveDTO) {
        DateTime now = new DateTime();
        Passenger passenger = BeanUtil.copyProperties(passengerSaveDTO, Passenger.class);
        if (ObjectUtil.isNull(passenger.getId())) {
            passenger.setMemberId(LoginMemberContext.getId());
            passenger.setId(SnowUtil.getSnowflakeNextId());
            passenger.setCreateTime(now);
            passenger.setUpdateTime(now);
            passengerMapper.insert(passenger);
        } else {
            passenger.setUpdateTime(now);
            passengerMapper.updateByPrimaryKey(passenger);
        }

    }

    public PageResp<PassengerQueryResp> queryList(PassengerQueryDTO passengerQueryDTO) {
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
        PageResp<PassengerQueryResp> pageDTO = new PageResp<>();
        pageDTO.setTotal(pageInfo.getTotal());
        pageDTO.setData(passengerQueryResps);

        return pageDTO;
    }

    public void delete(Long id) {
        passengerMapper.deleteByPrimaryKey(id);
    }

    //查询我的所有的乘客
    public List<PassengerQueryResp> queryMyPassenger() {
        PassengerExample passengerExample = new PassengerExample();
        PassengerExample.Criteria criteria = passengerExample.createCriteria();
        criteria.andMemberIdEqualTo(LoginMemberContext.getId());
        List<Passenger> passengers = passengerMapper.selectByExample(passengerExample);
        return BeanUtil.copyToList(passengers, PassengerQueryResp.class);
    }
}

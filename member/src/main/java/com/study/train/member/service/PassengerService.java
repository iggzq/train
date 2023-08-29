package com.study.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.util.SnowUtil;
import com.study.train.member.domain.Passenger;
import com.study.train.member.dto.PassengerSaveDTO;
import com.study.train.member.mapper.PassengerMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PassengerService {

    @Resource
    PassengerMapper passengerMapper;

    public void save(PassengerSaveDTO passengerSaveDTO){
        Date date = new Date();
        Passenger passenger = BeanUtil.copyProperties(passengerSaveDTO, Passenger.class);
        passenger.setMemberId(LoginMemberContext.getId());
        passenger.setId(SnowUtil.getSnowflakeNextId());
        passenger.setCreateTime(date);
        passenger.setUpdateTime(date);
        passengerMapper.insert(passenger);
    }
}

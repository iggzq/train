package com.study.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.common.util.SnowUtil;
import com.study.train.member.domain.Ticket;
import com.study.train.member.domain.TicketExample;
import com.study.train.common.resp.PageResp;
import com.study.train.member.dto.TicketQueryDTO;
import com.study.train.member.dto.TicketSaveDTO;
import com.study.train.member.mapper.TicketMapper;
import com.study.train.member.resp.TicketQueryResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    @Resource
    TicketMapper ticketMapper;

    public void save(TicketSaveDTO ticketSaveDTO) {
        DateTime now = new DateTime();
        Ticket ticket = BeanUtil.copyProperties(ticketSaveDTO, Ticket.class);
        if (ObjectUtil.isNull(ticket.getId())) {
            ticket.setId(SnowUtil.getSnowflakeNextId());
            ticket.setCreateTime(now);
            ticket.setUpdateTime(now);
            ticketMapper.insert(ticket);
        }else {
            ticket.setUpdateTime(now);
            ticketMapper.updateByPrimaryKey(ticket);
        }

    }

    public PageResp<TicketQueryResp> queryList(TicketQueryDTO ticketQueryDTO) {
        TicketExample ticketExample = new TicketExample();
        TicketExample.Criteria criteria = ticketExample.createCriteria();
        PageHelper.startPage(ticketQueryDTO.getPage(), ticketQueryDTO.getSize());
        List<Ticket> tickets = ticketMapper.selectByExample(ticketExample);

        PageInfo<Ticket> pageInfo = new PageInfo<>(tickets);

        LOG.info("总行数：{}", pageInfo.getTotal());
        LOG.info("总页数:{}", pageInfo.getPages());

        List<TicketQueryResp> ticketQueryResps = BeanUtil.copyToList(tickets, TicketQueryResp.class);
        PageResp<TicketQueryResp> pageResp = new PageResp<>();
            pageResp.setTotal(pageInfo.getTotal());
            pageResp.setData(ticketQueryResps);

        return pageResp;
    }

    public void delete(Long id){
        ticketMapper.deleteByPrimaryKey(id);
    }
}

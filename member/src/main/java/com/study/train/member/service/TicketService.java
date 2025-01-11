package com.study.train.member.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.study.train.common.req.MemberTicketReq;
import com.study.train.common.resp.PageResp;
import com.study.train.member.domain.Ticket;
import com.study.train.member.domain.TicketExample;
import com.study.train.member.req.TicketQueryReq;
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

    public void save(MemberTicketReq memberTicketReq) {
        DateTime now = new DateTime();
        Ticket ticket = BeanUtil.copyProperties(memberTicketReq, Ticket.class);
        ticket.setCreateTime(now);
        ticket.setUpdateTime(now);
        ticket.setStatus(memberTicketReq.getStatus());
        ticketMapper.insert(ticket);
    }

    public void update(MemberTicketReq memberTicketReq) {
        DateTime now = new DateTime();
        Ticket ticket = BeanUtil.copyProperties(memberTicketReq, Ticket.class);
        ticket.setUpdateTime(now);
        ticketMapper.updateByPrimaryKeySelective(ticket);
    }

    public PageResp<TicketQueryResp> queryList(TicketQueryReq ticketQueryReq) {
        TicketExample ticketExample = new TicketExample();
        ticketExample.setOrderByClause("`create_time` desc");
        TicketExample.Criteria criteria = ticketExample.createCriteria();
        if(ObjectUtil.isNotNull(ticketQueryReq.getMemberId())){
            criteria.andMemberIdEqualTo(ticketQueryReq.getMemberId());
        }
        PageHelper.startPage(ticketQueryReq.getPage(), ticketQueryReq.getSize());
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

    public void delete(Long id) {
        ticketMapper.deleteByPrimaryKey(id);
    }
}

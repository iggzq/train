package com.study.train.business.service;

import com.study.train.business.domain.ConfirmOrder;
import com.study.train.business.domain.DailyTrainStationSeat;
import com.study.train.business.domain.DailyTrainTicket;
import com.study.train.business.dto.ConfirmOrderTicketDTO;
import com.study.train.business.enums.ConfirmOrderStatusEnum;
import com.study.train.business.feign.MemberFeign;
import com.study.train.business.mapper.ConfirmOrderMapper;
import com.study.train.business.mapper.DailyTrainStationSeatMapper;
import com.study.train.business.mapper.customer.DailyTrainTicketMapperCust;
import com.study.train.common.context.LoginMemberContext;
import com.study.train.common.req.MemberTicketReq;
import com.study.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AfterConfirmOrderService {
    private static final Logger LOG = LoggerFactory.getLogger(ConfirmOrderService.class);

    @Resource
    private DailyTrainStationSeatMapper dailyTrainStationSeatMapper;
    @Resource
    private DailyTrainTicketMapperCust dailyTrainTicketMapperCust;
    @Resource
    private MemberFeign memberFeign;
    @Resource
    private ConfirmOrderMapper confirmOrderMapper;

    @Transactional
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket, List<DailyTrainStationSeat> finalSeats, List<ConfirmOrderTicketDTO> tickets, ConfirmOrder confirmOrder) {
        for (int j = 0; j < finalSeats.size(); j++) {
            DailyTrainStationSeat dailyTrainSeat = finalSeats.get(j);
            DailyTrainStationSeat updateSeat = new DailyTrainStationSeat();
            updateSeat.setId(dailyTrainSeat.getId());
            updateSeat.setSell(dailyTrainSeat.getSell());
            updateSeat.setUpdateTime(new Date());
            dailyTrainStationSeatMapper.updateByPrimaryKeySelective(updateSeat);
            Integer startIndex = dailyTrainTicket.getStartIndex();
            Integer endIndex = dailyTrainTicket.getEndIndex();
            char[] charArray = updateSeat.getSell().toCharArray();
            Integer maxStartIndex = endIndex - 1;
            Integer minEndIndex = startIndex + 1;
            int minStartIndex = 0;
            for (int i = startIndex - 1; i >= 0; i--) {
                char aChar = charArray[i];
                System.out.println(i);
                System.out.println(aChar);
                if (aChar == '1') {
                    minStartIndex = i + 1;//1
                    break;
                }
            }
            int maxEndIndex = updateSeat.getSell().length() + 1;
            for (int i = endIndex; i < updateSeat.getSell().length(); i++) {
                char aChar = charArray[i];
                if (aChar == '1') {
                    maxEndIndex = i;
                    break;
                }
            }
            System.out.println(minStartIndex);
            System.out.println(maxStartIndex);
            System.out.println(minEndIndex);
            System.out.println(maxEndIndex);
            dailyTrainTicketMapperCust.updateCountBySell(
                    dailyTrainSeat.getDate(),
                    dailyTrainSeat.getTrainCode(),
                    dailyTrainSeat.getSeatType(),
                    minStartIndex,
                    maxStartIndex,
                    minEndIndex,
                    maxEndIndex
            );

            // 调用会员服务接口，为会员增加一张车票
            MemberTicketReq memberTicketReq = new MemberTicketReq();
            memberTicketReq.setMemberId(LoginMemberContext.getId());
            memberTicketReq.setPassengerId(tickets.get(j).getPassengerId());
            memberTicketReq.setPassengerName(tickets.get(j).getPassengerName());
            memberTicketReq.setTrainDate(dailyTrainTicket.getDate());
            memberTicketReq.setTrainCode(dailyTrainTicket.getTrainCode());
            memberTicketReq.setCarriageIndex(dailyTrainSeat.getCarriageIndex());
            memberTicketReq.setSeatRow(dailyTrainSeat.getRow());
            memberTicketReq.setSeatCol(dailyTrainSeat.getCol());
            memberTicketReq.setStartStation(dailyTrainTicket.getStart());
            memberTicketReq.setStartTime(dailyTrainTicket.getStartTime());
            memberTicketReq.setEndStation(dailyTrainTicket.getEnd());
            memberTicketReq.setEndTime(dailyTrainTicket.getEndTime());
            memberTicketReq.setSeatType(dailyTrainSeat.getSeatType());
            CommonResp<Object> commonResp = memberFeign.save(memberTicketReq);
            LOG.info("调用member接口，返回：{}", commonResp);

            ConfirmOrder confirmOrderForUpdate = new ConfirmOrder();
            confirmOrderForUpdate.setId(confirmOrder.getId());
            confirmOrderForUpdate.setUpdateTime(new Date());
            confirmOrderForUpdate.setStatus(ConfirmOrderStatusEnum.SUCCESS.getCode());
            confirmOrderMapper.updateByPrimaryKeySelective(confirmOrderForUpdate);


        }
    }
}

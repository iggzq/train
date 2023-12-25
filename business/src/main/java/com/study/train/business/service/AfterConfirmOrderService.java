package com.study.train.business.service;

import com.study.train.business.domain.DailyTrainStationSeat;
import com.study.train.business.domain.DailyTrainTicket;
import com.study.train.business.mapper.DailyTrainStationSeatMapper;
import com.study.train.business.mapper.customer.DailyTrainTicketMapperCust;
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

    @Transactional
    public void afterDoConfirm(DailyTrainTicket dailyTrainTicket, List<DailyTrainStationSeat> dailyTrainStationSeat) {
        for (DailyTrainStationSeat trainStationSeat : dailyTrainStationSeat) {
            DailyTrainStationSeat updateSeat = new DailyTrainStationSeat();
            updateSeat.setId(trainStationSeat.getId());
            updateSeat.setSell(trainStationSeat.getSell());
            updateSeat.setUpdateTime(new Date());
            dailyTrainStationSeatMapper.updateByPrimaryKeySelective(updateSeat);
            Integer startIndex = dailyTrainTicket.getStartIndex();
            Integer endIndex = dailyTrainTicket.getEndIndex();
            char[] charArray = updateSeat.getSell().toCharArray();
            Integer maxStartIndex = endIndex - 1;
            Integer minEndIndex = startIndex - 1;
            int minStartIndex = 0;
            for (int i = startIndex - 1; i >= 0; i--) {
                char aChar = charArray[i];
                if (aChar == '1') {
                    minStartIndex = i + 1;
                    break;
                }
            }
            int maxEndIndex = updateSeat.getSell().length() ;
            for(int i = endIndex; i < updateSeat.getSell().length(); i++){
                char aChar = charArray[i];
                if(aChar == '1'){
                    maxEndIndex = i;
                    break;
                }
            }
            dailyTrainTicketMapperCust.updateCountBySell(
                    trainStationSeat.getDate(),
                    trainStationSeat.getTrainCode(),
                    trainStationSeat.getSeatType(),
                    minStartIndex,
                    maxStartIndex,
                    minEndIndex,
                    maxEndIndex
            );


        }
    }
}

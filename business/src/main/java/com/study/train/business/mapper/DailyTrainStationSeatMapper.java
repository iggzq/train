package com.study.train.business.mapper;

import com.study.train.business.domain.DailyTrainStationSeat;
import com.study.train.business.domain.DailyTrainStationSeatExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DailyTrainStationSeatMapper {
    long countByExample(DailyTrainStationSeatExample example);

    int deleteByExample(DailyTrainStationSeatExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DailyTrainStationSeat record);

    int insertSelective(DailyTrainStationSeat record);

    List<DailyTrainStationSeat> selectByExample(DailyTrainStationSeatExample example);

    DailyTrainStationSeat selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") DailyTrainStationSeat record, @Param("example") DailyTrainStationSeatExample example);

    int updateByExample(@Param("record") DailyTrainStationSeat record, @Param("example") DailyTrainStationSeatExample example);

    int updateByPrimaryKeySelective(DailyTrainStationSeat record);

    int updateByPrimaryKey(DailyTrainStationSeat record);
}
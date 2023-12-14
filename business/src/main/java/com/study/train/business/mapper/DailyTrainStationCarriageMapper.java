package com.study.train.business.mapper;

import com.study.train.business.domain.DailyTrainStationCarriage;
import com.study.train.business.domain.DailyTrainStationCarriageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DailyTrainStationCarriageMapper {
    long countByExample(DailyTrainStationCarriageExample example);

    int deleteByExample(DailyTrainStationCarriageExample example);

    int deleteByPrimaryKey(Long id);

    int insert(DailyTrainStationCarriage record);

    int insertSelective(DailyTrainStationCarriage record);

    List<DailyTrainStationCarriage> selectByExample(DailyTrainStationCarriageExample example);

    DailyTrainStationCarriage selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") DailyTrainStationCarriage record, @Param("example") DailyTrainStationCarriageExample example);

    int updateByExample(@Param("record") DailyTrainStationCarriage record, @Param("example") DailyTrainStationCarriageExample example);

    int updateByPrimaryKeySelective(DailyTrainStationCarriage record);

    int updateByPrimaryKey(DailyTrainStationCarriage record);
}
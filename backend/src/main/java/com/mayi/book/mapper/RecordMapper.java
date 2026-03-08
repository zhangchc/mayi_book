package com.mayi.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mayi.book.entity.Record;
import com.mayi.book.dto.RecordDTO;
import com.mayi.book.dto.RecordGroupDTO;
import com.mayi.book.dto.StatisticsResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RecordMapper extends BaseMapper<Record> {
    /**
     * 查询用户记账明细列表（按天分组）
     */
    List<RecordGroupDTO> selectRecordGroupList(@Param("userId") Long userId,
                                                @Param("month") String month,
                                                @Param("type") String type,
                                                @Param("offset") Integer offset,
                                                @Param("pageSize") Integer pageSize);

    /**
     * 查询指定日期的记账记录
     */
    List<RecordDTO> selectRecordsByDate(@Param("userId") Long userId,
                                         @Param("date") String date,
                                         @Param("type") String type);

    /**
     * 查询用户记账明细总数
     */
    Long countRecordGroup(@Param("userId") Long userId,
                          @Param("month") String month,
                          @Param("type") String type);

    /**
     * 查询月度统计
     */
    StatisticsResponse selectStatistics(@Param("userId") Long userId,
                                        @Param("month") String month);
}

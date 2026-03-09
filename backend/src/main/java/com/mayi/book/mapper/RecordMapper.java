package com.mayi.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mayi.book.entity.Record;
import com.mayi.book.dto.RecordDTO;
import com.mayi.book.dto.RecordGroupDTO;
import com.mayi.book.dto.StatisticsResponse;
import com.mayi.book.dto.DailyAmountDTO;
import com.mayi.book.dto.CategoryExpenseRankDTO;
import com.mayi.book.dto.MonthlyAmountDTO;
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
                                                @Param("type") Integer type,
                                                @Param("offset") Integer offset,
                                                @Param("pageSize") Integer pageSize);

    /**
     * 查询指定日期的记账记录
     */
    List<RecordDTO> selectRecordsByDate(@Param("userId") Long userId,
                                         @Param("date") String date,
                                         @Param("type") Integer type);

    /**
     * 查询用户记账明细总数
     */
    Long countRecordGroup(@Param("userId") Long userId,
                          @Param("month") String month,
                          @Param("type") Integer type);

    /**
     * 查询月度统计
     */
    StatisticsResponse selectStatistics(@Param("userId") Long userId,
                                        @Param("month") String month);

    /**
     * 按天汇总指定日期范围内的金额数据
     */
    List<DailyAmountDTO> selectDailyAmountRange(@Param("userId") Long userId,
                                                @Param("type") Integer type,
                                                @Param("startDate") java.time.LocalDate startDate,
                                                @Param("endDate") java.time.LocalDate endDate);

    /**
     * 支出排行榜：按分类汇总指定日期范围内的支出，金额从高到低
     */
    List<CategoryExpenseRankDTO> selectExpenseRankByDateRange(@Param("userId") Long userId,
                                                              @Param("startDate") java.time.LocalDate startDate,
                                                              @Param("endDate") java.time.LocalDate endDate);

    /**
     * 按年按月汇总金额（用于年维度图表）
     */
    List<MonthlyAmountDTO> selectMonthlyAmountByYear(@Param("userId") Long userId,
                                                    @Param("type") Integer type,
                                                    @Param("year") Integer year);
}

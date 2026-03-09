package com.mayi.book.service;

import com.mayi.book.dto.*;
import com.mayi.book.entity.Record;
import com.mayi.book.mapper.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class RecordService {
    
    @Autowired
    private RecordMapper recordMapper;
    
    @Autowired
    private CategoryService categoryService;

    @Transactional
    public RecordDTO addRecord(Long userId, RecordAddRequest request) {
        // 验证分类是否存在
        com.mayi.book.entity.Category category = categoryService.getCategoryById(request.getCategoryId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 创建记账记录。type: 1-支出 2-收入，默认 1
        Record record = new Record();
        record.setUserId(userId);
        record.setCategoryId(request.getCategoryId());
        record.setType(request.getType() != null ? request.getType() : 1);
        record.setAmount(request.getAmount());
        record.setRemark(request.getRemark());
        record.setRecordDate(LocalDate.now());
        
        recordMapper.insert(record);
        
        // 返回DTO（含分类 id、名称、图标，便于前端按数据展示）
        RecordDTO dto = new RecordDTO();
        dto.setId(record.getId());
        dto.setType(record.getType());
        dto.setCategoryId(category.getId());
        dto.setCategoryName(category.getName());
        dto.setCategoryIcon(category.getIcon());
        dto.setAmount(record.getAmount());
        dto.setRemark(record.getRemark());
        dto.setCreateTime(record.getCreateTime());
        
        return dto;
    }

    public RecordListResponse getRecordList(Long userId, RecordListRequest request) {
        // 计算偏移量
        Integer offset = (request.getPage() - 1) * request.getPageSize();
        
        // 查询分组数据（按天分组，包含汇总信息）
        List<RecordGroupDTO> groupList = recordMapper.selectRecordGroupList(
                userId, request.getMonth(), request.getType(), offset, request.getPageSize());
        
        // 查询总数
        Long total = recordMapper.countRecordGroup(userId, request.getMonth(), request.getType());
        
        // 为每个日期组查询详细的记账记录
        for (RecordGroupDTO group : groupList) {
            List<RecordDTO> records = recordMapper.selectRecordsByDate(userId, group.getDate(), request.getType());
            group.setRecords(records);
        }
        
        RecordListResponse response = new RecordListResponse();
        response.setTotal(total);
        response.setList(groupList);
        
        return response;
    }

    public StatisticsResponse getStatistics(Long userId, String month) {
        return recordMapper.selectStatistics(userId, month);
    }

    /**
     * 按周统计：根据日期范围（通常是一周）统计总金额与每日明细
     */
    public WeeklyStatisticsResponse getWeeklyStatistics(Long userId, LocalDate startDate, LocalDate endDate, Integer type) {
        if (startDate == null || endDate == null) {
            // 默认使用当前周（周一到周日）
            LocalDate today = LocalDate.now();
            int dayOfWeek = today.getDayOfWeek().getValue(); // 1-7 周一到周日
            startDate = today.minusDays(dayOfWeek - 1L);
            endDate = startDate.plusDays(6);
        }
        if (type == null) {
            type = 1; // 1-支出
        }

        // 查询每日金额
        java.util.List<DailyAmountDTO> dailyList = recordMapper.selectDailyAmountRange(userId, type, startDate, endDate);

        // 计算总额与平均值（按天平均）
        BigDecimal total = BigDecimal.ZERO;
        for (DailyAmountDTO dto : dailyList) {
            if (dto.getAmount() != null) {
                total = total.add(dto.getAmount());
            }
        }
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        BigDecimal average = days > 0
                ? total.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        WeeklyStatisticsResponse resp = new WeeklyStatisticsResponse();
        resp.setStartDate(startDate.toString());
        resp.setEndDate(endDate.toString());
        resp.setTotalAmount(total);
        resp.setAverageAmount(average);
        resp.setDailyList(dailyList);
        return resp;
    }

    /**
     * 支出排行榜：按分类汇总指定日期范围内的支出，从高到低，并计算占比
     */
    public List<CategoryExpenseRankDTO> getExpenseRanking(Long userId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            LocalDate today = LocalDate.now();
            int dayOfWeek = today.getDayOfWeek().getValue();
            startDate = today.minusDays(dayOfWeek - 1L);
            endDate = startDate.plusDays(6);
        }
        List<CategoryExpenseRankDTO> list = recordMapper.selectExpenseRankByDateRange(userId, startDate, endDate);
        BigDecimal total = BigDecimal.ZERO;
        for (CategoryExpenseRankDTO dto : list) {
            if (dto.getAmount() != null) {
                total = total.add(dto.getAmount());
            }
        }
        if (total.compareTo(BigDecimal.ZERO) > 0) {
            for (CategoryExpenseRankDTO dto : list) {
                BigDecimal pct = dto.getAmount() != null
                        ? dto.getAmount().divide(total, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                        : BigDecimal.ZERO;
                dto.setPercentage(pct.setScale(2, RoundingMode.HALF_UP));
            }
        } else {
            for (CategoryExpenseRankDTO dto : list) {
                dto.setPercentage(BigDecimal.ZERO);
            }
        }
        return list;
    }

    /**
     * 按年统计：每月金额汇总，总金额，月均
     */
    public com.mayi.book.dto.YearStatisticsResponse getYearStatistics(Long userId, Integer year, Integer type) {
        if (year == null) {
            year = java.time.Year.now().getValue();
        }
        if (type == null) {
            type = 1; // 1-支出
        }
        List<com.mayi.book.dto.MonthlyAmountDTO> monthlyList = recordMapper.selectMonthlyAmountByYear(userId, type, year);
        BigDecimal total = BigDecimal.ZERO;
        for (com.mayi.book.dto.MonthlyAmountDTO dto : monthlyList) {
            if (dto.getAmount() != null) {
                total = total.add(dto.getAmount());
            }
        }
        BigDecimal average = total.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
        com.mayi.book.dto.YearStatisticsResponse resp = new com.mayi.book.dto.YearStatisticsResponse();
        resp.setYear(year);
        resp.setTotalAmount(total);
        resp.setAverageAmount(average);
        resp.setMonthlyList(monthlyList);
        return resp;
    }
}

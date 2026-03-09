package com.mayi.book.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 按周统计的支出/收入数据
 */
public class WeeklyStatisticsResponse {

    /**
     * 统计开始日期 yyyy-MM-dd
     */
    private String startDate;

    /**
     * 统计结束日期 yyyy-MM-dd
     */
    private String endDate;

    /**
     * 本周总支出/收入
     */
    private BigDecimal totalAmount;

    /**
     * 平均值（按天平均）
     */
    private BigDecimal averageAmount;

    /**
     * 每天的金额明细
     */
    private List<DailyAmountDTO> dailyList;

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAverageAmount() {
        return averageAmount;
    }

    public void setAverageAmount(BigDecimal averageAmount) {
        this.averageAmount = averageAmount;
    }

    public List<DailyAmountDTO> getDailyList() {
        return dailyList;
    }

    public void setDailyList(List<DailyAmountDTO> dailyList) {
        this.dailyList = dailyList;
    }
}


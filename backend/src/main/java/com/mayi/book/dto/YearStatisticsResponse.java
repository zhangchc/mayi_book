package com.mayi.book.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 按年统计：总金额、月均、每月明细
 */
public class YearStatisticsResponse {
    private Integer year;
    private BigDecimal totalAmount;
    private BigDecimal averageAmount;
    private List<MonthlyAmountDTO> monthlyList;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
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

    public List<MonthlyAmountDTO> getMonthlyList() {
        return monthlyList;
    }

    public void setMonthlyList(List<MonthlyAmountDTO> monthlyList) {
        this.monthlyList = monthlyList;
    }
}

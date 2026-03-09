package com.mayi.book.dto;

import java.math.BigDecimal;

/**
 * 按月汇总的金额
 */
public class MonthlyAmountDTO {
    /** 月份 yyyy-MM */
    private String month;
    private BigDecimal amount;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

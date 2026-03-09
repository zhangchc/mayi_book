package com.mayi.book.dto;

import java.math.BigDecimal;

/**
 * 按天汇总的金额数据
 */
public class DailyAmountDTO {

    /**
     * 日期，格式：yyyy-MM-dd
     */
    private String date;

    /**
     * 当日金额（支出或收入，根据查询类型决定）
     */
    private BigDecimal amount;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}


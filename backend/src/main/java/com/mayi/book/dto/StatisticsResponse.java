package com.mayi.book.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class StatisticsResponse {
    private BigDecimal totalExpense;  // 总支出
    private BigDecimal totalIncome;   // 总收入
    private BigDecimal balance;       // 余额（收入-支出）
}

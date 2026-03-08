package com.mayi.book.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RecordGroupDTO {
    private String date;  // 日期：2024-03-08
    private String dayOfWeek;  // 星期：星期日
    private BigDecimal totalExpense;  // 当天总支出
    private BigDecimal totalIncome;    // 当天总收入
    private List<RecordDTO> records;   // 当天的记账记录列表
}

package com.mayi.book.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RecordAddRequest {
    private Long categoryId;
    private BigDecimal amount;
    private String remark;
    private String type;  // expense-支出, income-收入
}

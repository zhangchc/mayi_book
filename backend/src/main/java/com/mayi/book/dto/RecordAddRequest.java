package com.mayi.book.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RecordAddRequest {
    private Long categoryId;
    private BigDecimal amount;
    private String remark;
    private Integer type;  // 1-支出, 2-收入
}

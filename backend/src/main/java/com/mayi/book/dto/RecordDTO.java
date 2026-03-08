package com.mayi.book.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecordDTO {
    private Long id;
    private String type;  // expense-支出, income-收入
    private String categoryName;
    private BigDecimal amount;
    private String remark;
    private LocalDateTime createTime;
}

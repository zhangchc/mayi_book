package com.mayi.book.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecordDTO {
    private Long id;
    private Integer type;  // 1-支出, 2-收入
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;  // 分类图标（来自 category.icon，按 id 对应展示）
    private BigDecimal amount;
    private String remark;
    private LocalDateTime createTime;
}

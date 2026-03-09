package com.mayi.book.dto;

import lombok.Data;

@Data
public class RecordListRequest {
    private Integer page = 1;
    private Integer pageSize = 20;
    private String month;  // 格式：2024-03
    private Integer type;   // 1-支出, 2-收入
}

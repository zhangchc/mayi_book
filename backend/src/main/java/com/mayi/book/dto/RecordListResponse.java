package com.mayi.book.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecordListResponse {
    private Long total;
    private List<RecordGroupDTO> list;
}

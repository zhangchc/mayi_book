package com.mayi.book.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("record")
public class Record {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long categoryId;
    
    private Integer type;  // 1-支出, 2-收入
    
    private BigDecimal amount;
    
    private String remark;
    
    private LocalDate recordDate;
    
    @TableLogic
    private Integer delFlag;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

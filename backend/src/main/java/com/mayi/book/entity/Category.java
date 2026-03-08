package com.mayi.book.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("category")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    
    private String type;  // expense-支出, income-收入
    
    private String icon;
    
    private Integer sortOrder;
    
    private Integer status;  // 1-启用, 0-禁用
    
    @TableLogic
    private Integer delFlag;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

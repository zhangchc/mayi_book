package com.mayi.book.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String openid;
    
    private String nickName;
    
    private String avatarUrl;
    
    @TableLogic
    private Integer delFlag;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}

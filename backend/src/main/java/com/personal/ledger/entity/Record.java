package com.personal.ledger.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("record")
public class Record {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Integer type;
    private BigDecimal amount;
    private Long categoryId;
    private String remark;
    private LocalDateTime recordTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    @TableLogic
    private Integer deleted;
}

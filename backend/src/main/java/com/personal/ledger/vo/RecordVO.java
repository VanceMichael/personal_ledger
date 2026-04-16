package com.personal.ledger.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecordVO {
    private Long id;
    private Long userId;
    private Integer type;
    private BigDecimal amount;
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
    private String remark;
    private LocalDateTime recordTime;
    private LocalDateTime createTime;
}

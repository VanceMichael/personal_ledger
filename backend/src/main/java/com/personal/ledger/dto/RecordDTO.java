package com.personal.ledger.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecordDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @NotNull(message = "类型不能为空")
    private Integer type;
    @NotNull(message = "金额不能为空")
    private BigDecimal amount;
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
    private String remark;
    @NotNull(message = "记录时间不能为空")
    private LocalDateTime recordTime;
}

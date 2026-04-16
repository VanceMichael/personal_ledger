package com.personal.ledger.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class BudgetDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @NotNull(message = "预算金额不能为空")
    private BigDecimal amount;
    @NotNull(message = "年份不能为空")
    private Integer year;
    @NotNull(message = "月份不能为空")
    private Integer month;
}

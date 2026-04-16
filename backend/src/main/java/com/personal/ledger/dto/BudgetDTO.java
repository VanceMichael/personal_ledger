package com.personal.ledger.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BudgetDTO {
    @NotNull(message = "年份不能为空")
    private Integer year;
    @NotNull(message = "月份不能为空")
    private Integer month;
    @NotNull(message = "预算金额不能为空")
    private BigDecimal amount;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

package com.personal.ledger.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class StatisticVO {
    private BigDecimal totalExpense;
    private BigDecimal totalIncome;
    private BigDecimal balance;
    private List<CategoryStatVO> categoryStats;
    private List<DailyStatVO> dailyStats;
    private BigDecimal budgetAmount;
    private BigDecimal budgetUsed;
    private BigDecimal budgetRemaining;
    private Integer budgetUsagePercent;
}

@Data
class CategoryStatVO {
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
    private BigDecimal amount;
    private Double percent;
}

@Data
class DailyStatVO {
    private String date;
    private BigDecimal expense;
    private BigDecimal income;
}

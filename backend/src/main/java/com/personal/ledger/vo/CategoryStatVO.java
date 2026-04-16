package com.personal.ledger.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CategoryStatVO {
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
    private BigDecimal amount;
    private Double percent;
}

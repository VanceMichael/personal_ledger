package com.personal.ledger.vo;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class DailyStatVO {
    private String date;
    private BigDecimal expense;
    private BigDecimal income;
}

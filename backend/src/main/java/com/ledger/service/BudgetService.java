package com.ledger.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.entity.Budget;

public interface BudgetService extends IService<Budget> {
    Budget getByMonth(Long userId, String month);
}

package com.personal.ledger.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.personal.ledger.dto.BudgetDTO;
import com.personal.ledger.entity.Budget;
import com.personal.ledger.vo.StatisticVO;

public interface BudgetService extends IService<Budget> {
    Budget setBudget(BudgetDTO dto);
    Budget getBudget(Long userId, Integer year, Integer month);
    StatisticVO getMonthStatistic(Long userId, Integer year, Integer month);
    StatisticVO getYearStatistic(Long userId, Integer year);
}

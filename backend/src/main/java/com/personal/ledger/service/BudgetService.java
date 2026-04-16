package com.personal.ledger.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personal.ledger.dto.BudgetDTO;
import com.personal.ledger.entity.Budget;
import com.personal.ledger.mapper.BudgetMapper;
import com.personal.ledger.mapper.TransactionMapper;
import com.personal.ledger.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Service
public class BudgetService extends ServiceImpl<BudgetMapper, Budget> {
    @Autowired
    private TransactionMapper transactionMapper;

    public void setBudget(BudgetDTO dto) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Budget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Budget::getUserId, userId)
                .eq(Budget::getYear, dto.getYear())
                .eq(Budget::getMonth, dto.getMonth());
        Budget budget = getOne(wrapper);
        if (budget == null) {
            budget = new Budget();
            budget.setUserId(userId);
            budget.setYear(dto.getYear());
            budget.setMonth(dto.getMonth());
            budget.setAmount(dto.getAmount());
            save(budget);
        } else {
            budget.setAmount(dto.getAmount());
            updateById(budget);
        }
    }

    public Map<String, Object> getBudgetProgress(Integer year, Integer month) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Budget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Budget::getUserId, userId)
                .eq(Budget::getYear, year)
                .eq(Budget::getMonth, month);
        Budget budget = getOne(wrapper);

        BigDecimal totalExpense = transactionMapper.selectTotalExpense(userId, year, month);
        BigDecimal budgetAmount = budget != null ? budget.getAmount() : BigDecimal.ZERO;

        int progress = 0;
        String status = "normal";
        if (budgetAmount.compareTo(BigDecimal.ZERO) > 0) {
            progress = totalExpense.multiply(BigDecimal.valueOf(100))
                    .divide(budgetAmount, 0, RoundingMode.HALF_UP).intValue();
            if (progress >= 100) {
                status = "over";
            } else if (progress >= 80) {
                status = "warning";
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("budgetAmount", budgetAmount);
        result.put("usedAmount", totalExpense);
        result.put("remainingAmount", budgetAmount.subtract(totalExpense).max(BigDecimal.ZERO));
        result.put("progress", Math.min(progress, 100));
        result.put("status", status);
        return result;
    }
}

package com.ledger.controller;

import com.ledger.common.Result;
import com.ledger.entity.Budget;
import com.ledger.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping("/{userId}/{month}")
    public Result<Budget> getByMonth(@PathVariable Long userId, @PathVariable String month) {
        return Result.success(budgetService.getByMonth(userId, month));
    }

    @PostMapping("/save")
    public Result<Budget> save(@RequestBody Budget budget) {
        Budget exist = budgetService.getByMonth(budget.getUserId(), budget.getMonth());
        if (exist != null) {
            budget.setId(exist.getId());
            budgetService.updateById(budget);
        } else {
            budgetService.save(budget);
        }
        return Result.success(budget);
    }
}

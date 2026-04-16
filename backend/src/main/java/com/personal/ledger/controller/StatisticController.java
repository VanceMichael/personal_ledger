package com.personal.ledger.controller;

import com.personal.ledger.dto.BudgetDTO;
import com.personal.ledger.entity.Budget;
import com.personal.ledger.service.BudgetService;
import com.personal.ledger.utils.Result;
import com.personal.ledger.vo.StatisticVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

@RestController
@RequestMapping("/statistic")
@CrossOrigin
public class StatisticController {

    @Resource
    private BudgetService budgetService;

    @PostMapping("/budget")
    public Result<Budget> setBudget(@Valid @RequestBody BudgetDTO dto) {
        try {
            Budget budget = budgetService.setBudget(dto);
            return Result.success(budget);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/budget")
    public Result<Budget> getBudget(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        Budget budget = budgetService.getBudget(userId, year, month);
        return Result.success(budget);
    }

    @GetMapping("/month")
    public Result<StatisticVO> getMonthStatistic(
            @RequestParam Long userId,
            @RequestParam Integer year,
            @RequestParam Integer month) {
        StatisticVO statistic = budgetService.getMonthStatistic(userId, year, month);
        return Result.success(statistic);
    }

    @GetMapping("/year")
    public Result<StatisticVO> getYearStatistic(
            @RequestParam Long userId,
            @RequestParam Integer year) {
        StatisticVO statistic = budgetService.getYearStatistic(userId, year);
        return Result.success(statistic);
    }
}

package com.personal.ledger.controller;

import com.personal.ledger.common.Result;
import com.personal.ledger.dto.BudgetDTO;
import com.personal.ledger.service.BudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/budget")
public class BudgetController {
    @Autowired
    private BudgetService budgetService;

    @PostMapping("/set")
    public Result<Void> setBudget(@Valid @RequestBody BudgetDTO dto) {
        try {
            budgetService.setBudget(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/progress")
    public Result<Map<String, Object>> getBudgetProgress(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        try {
            Map<String, Object> result = budgetService.getBudgetProgress(year, month);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

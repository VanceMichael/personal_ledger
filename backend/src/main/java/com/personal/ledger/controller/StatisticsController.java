package com.personal.ledger.controller;

import com.personal.ledger.common.Result;
import com.personal.ledger.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/monthly-summary")
    public Result<Map<String, Object>> getMonthlySummary(
            @RequestParam Integer year,
            @RequestParam Integer month) {
        try {
            Map<String, Object> result = statisticsService.getMonthlySummary(year, month);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/category-stat")
    public Result<List<Map<String, Object>>> getCategoryStat(
            @RequestParam Integer type,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            List<Map<String, Object>> result = statisticsService.getCategoryStat(type, startDate, endDate);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/trend-stat")
    public Result<List<Map<String, Object>>> getTrendStat(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            List<Map<String, Object>> result = statisticsService.getTrendStat(startDate, endDate);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/annual-summary")
    public Result<Map<String, Object>> getAnnualSummary(@RequestParam Integer year) {
        try {
            Map<String, Object> result = statisticsService.getAnnualSummary(year);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

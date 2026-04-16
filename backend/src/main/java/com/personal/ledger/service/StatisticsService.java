package com.personal.ledger.service;

import com.personal.ledger.mapper.TransactionMapper;
import com.personal.ledger.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {
    @Autowired
    private TransactionMapper transactionMapper;

    public Map<String, Object> getMonthlySummary(Integer year, Integer month) {
        Long userId = UserContext.getUserId();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Map<String, Object>> incomeList = transactionMapper.selectCategoryStat(
                userId, 1, startDate.toString(), endDate.toString());
        List<Map<String, Object>> expenseList = transactionMapper.selectCategoryStat(
                userId, 2, startDate.toString(), endDate.toString());

        BigDecimal totalIncome = incomeList.stream()
                .map(m -> (BigDecimal) m.get("value"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalExpense = expenseList.stream()
                .map(m -> (BigDecimal) m.get("value"))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> result = new HashMap<>();
        result.put("totalIncome", totalIncome);
        result.put("totalExpense", totalExpense);
        result.put("balance", totalIncome.subtract(totalExpense));
        result.put("incomeList", incomeList);
        result.put("expenseList", expenseList);
        return result;
    }

    public List<Map<String, Object>> getCategoryStat(Integer type, String startDate, String endDate) {
        Long userId = UserContext.getUserId();
        return transactionMapper.selectCategoryStat(userId, type, startDate, endDate);
    }

    public List<Map<String, Object>> getTrendStat(String startDate, String endDate) {
        Long userId = UserContext.getUserId();
        return transactionMapper.selectTrendStat(userId, startDate, endDate);
    }

    public Map<String, Object> getAnnualSummary(Integer year) {
        Long userId = UserContext.getUserId();
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        List<Map<String, Object>> trendList = transactionMapper.selectTrendStat(
                userId, startDate.toString(), endDate.toString());

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        for (Map<String, Object> item : trendList) {
            totalIncome = totalIncome.add((BigDecimal) item.get("income"));
            totalExpense = totalExpense.add((BigDecimal) item.get("expense"));
        }

        Map<String, Object> result = new HashMap<>();
        result.put("totalIncome", totalIncome);
        result.put("totalExpense", totalExpense);
        result.put("balance", totalIncome.subtract(totalExpense));
        result.put("monthlyData", trendList);
        return result;
    }
}

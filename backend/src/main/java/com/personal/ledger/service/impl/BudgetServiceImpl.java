package com.personal.ledger.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personal.ledger.dto.BudgetDTO;
import com.personal.ledger.entity.Budget;
import com.personal.ledger.entity.Category;
import com.personal.ledger.entity.Record;
import com.personal.ledger.mapper.BudgetMapper;
import com.personal.ledger.service.BudgetService;
import com.personal.ledger.service.CategoryService;
import com.personal.ledger.service.RecordService;
import com.personal.ledger.vo.*;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl extends ServiceImpl<BudgetMapper, Budget> implements BudgetService {

    @Resource
    private RecordService recordService;
    @Resource
    private CategoryService categoryService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public Budget setBudget(BudgetDTO dto) {
        LambdaQueryWrapper<Budget> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Budget::getUserId, dto.getUserId())
                .eq(Budget::getYear, dto.getYear())
                .eq(Budget::getMonth, dto.getMonth());
        Budget existBudget = getOne(queryWrapper);
        if (existBudget != null) {
            existBudget.setAmount(dto.getAmount());
            updateById(existBudget);
            return existBudget;
        }
        Budget budget = new Budget();
        BeanUtils.copyProperties(dto, budget);
        save(budget);
        return budget;
    }

    @Override
    public Budget getBudget(Long userId, Integer year, Integer month) {
        LambdaQueryWrapper<Budget> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Budget::getUserId, userId)
                .eq(Budget::getYear, year)
                .eq(Budget::getMonth, month);
        return getOne(queryWrapper);
    }

    @Override
    public StatisticVO getMonthStatistic(Long userId, Integer year, Integer month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime startTime = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime endTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Record> records = recordService.list(new LambdaQueryWrapper<Record>()
                .eq(Record::getUserId, userId)
                .between(Record::getRecordTime, startTime, endTime));

        return buildStatisticVO(records, userId, year, month);
    }

    @Override
    public StatisticVO getYearStatistic(Long userId, Integer year) {
        LocalDateTime startTime = LocalDate.of(year, 1, 1).atStartOfDay();
        LocalDateTime endTime = LocalDate.of(year, 12, 31).atTime(23, 59, 59);

        List<Record> records = recordService.list(new LambdaQueryWrapper<Record>()
                .eq(Record::getUserId, userId)
                .between(Record::getRecordTime, startTime, endTime));

        StatisticVO statisticVO = buildStatisticVO(records, userId, null, null);
        
        Map<String, DailyStatVO> dailyStatMap = statisticVO.getDailyStats().stream()
                .collect(Collectors.toMap(DailyStatVO::getDate, d -> d));
        List<DailyStatVO> monthStats = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            String monthKey = String.format("%02d", i);
            BigDecimal expense = BigDecimal.ZERO;
            BigDecimal income = BigDecimal.ZERO;
            for (Map.Entry<String, DailyStatVO> entry : dailyStatMap.entrySet()) {
                if (entry.getKey().startsWith(year + "-" + monthKey)) {
                    expense = expense.add(entry.getValue().getExpense());
                    income = income.add(entry.getValue().getIncome());
                }
            }
            DailyStatVO monthStat = new DailyStatVO();
            monthStat.setDate(monthKey + "月");
            monthStat.setExpense(expense);
            monthStat.setIncome(income);
            monthStats.add(monthStat);
        }
        statisticVO.setDailyStats(monthStats);
        return statisticVO;
    }

    private StatisticVO buildStatisticVO(List<Record> records, Long userId, Integer year, Integer month) {
        StatisticVO statisticVO = new StatisticVO();

        BigDecimal totalExpense = BigDecimal.ZERO;
        BigDecimal totalIncome = BigDecimal.ZERO;
        for (Record record : records) {
            if (record.getType() == 1) {
                totalExpense = totalExpense.add(record.getAmount());
            } else {
                totalIncome = totalIncome.add(record.getAmount());
            }
        }
        statisticVO.setTotalExpense(totalExpense);
        statisticVO.setTotalIncome(totalIncome);
        statisticVO.setBalance(totalIncome.subtract(totalExpense));

        Map<Long, List<Record>> categoryRecordMap = records.stream()
                .filter(r -> r.getType() == 1)
                .collect(Collectors.groupingBy(Record::getCategoryId));

        List<CategoryStatVO> categoryStats = new ArrayList<>();
        if (!categoryRecordMap.isEmpty()) {
            List<Long> categoryIds = new ArrayList<>(categoryRecordMap.keySet());
            List<Category> categories = categoryService.listByIds(categoryIds);
            Map<Long, Category> categoryMap = categories.stream().collect(Collectors.toMap(Category::getId, c -> c));

            for (Map.Entry<Long, List<Record>> entry : categoryRecordMap.entrySet()) {
                Long categoryId = entry.getKey();
                BigDecimal amount = entry.getValue().stream()
                        .map(Record::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                double percent = totalExpense.compareTo(BigDecimal.ZERO) > 0
                        ? amount.divide(totalExpense, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).doubleValue()
                        : 0;
                Category category = categoryMap.get(categoryId);
                if (category != null) {
                    CategoryStatVO statVO = new CategoryStatVO();
                    statVO.setCategoryId(categoryId);
                    statVO.setCategoryName(category.getName());
                    statVO.setCategoryIcon(category.getIcon());
                    statVO.setAmount(amount);
                    statVO.setPercent(percent);
                    categoryStats.add(statVO);
                }
            }
            categoryStats.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));
        }
        statisticVO.setCategoryStats(categoryStats);

        Map<String, DailyStatVO> dailyStatMap = new TreeMap<>();
        for (Record record : records) {
            String date = record.getRecordTime().format(DATE_FORMATTER);
            DailyStatVO dailyStat = dailyStatMap.computeIfAbsent(date, k -> {
                DailyStatVO d = new DailyStatVO();
                d.setDate(date);
                d.setExpense(BigDecimal.ZERO);
                d.setIncome(BigDecimal.ZERO);
                return d;
            });
            if (record.getType() == 1) {
                dailyStat.setExpense(dailyStat.getExpense().add(record.getAmount()));
            } else {
                dailyStat.setIncome(dailyStat.getIncome().add(record.getAmount()));
            }
        }
        statisticVO.setDailyStats(new ArrayList<>(dailyStatMap.values()));

        if (year != null && month != null) {
            Budget budget = getBudget(userId, year, month);
            if (budget != null) {
                statisticVO.setBudgetAmount(budget.getAmount());
                statisticVO.setBudgetUsed(totalExpense);
                BigDecimal remaining = budget.getAmount().subtract(totalExpense);
                statisticVO.setBudgetRemaining(remaining.compareTo(BigDecimal.ZERO) > 0 ? remaining : BigDecimal.ZERO);
                int usagePercent = budget.getAmount().compareTo(BigDecimal.ZERO) > 0
                        ? totalExpense.divide(budget.getAmount(), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).intValue()
                        : 0;
                statisticVO.setBudgetUsagePercent(Math.min(usagePercent, 100));
            }
        }

        return statisticVO;
    }
}

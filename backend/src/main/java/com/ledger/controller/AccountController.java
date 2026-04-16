package com.ledger.controller;

import com.ledger.common.Result;
import com.ledger.dto.AccountDTO;
import com.ledger.entity.Account;
import com.ledger.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/list")
    public Result<List<Account>> list(@RequestBody AccountDTO dto) {
        return Result.success(accountService.listWithCategory(dto));
    }

    @PostMapping("/add")
    public Result<Account> add(@RequestBody Account account) {
        accountService.save(account);
        return Result.success(account);
    }

    @PutMapping("/update")
    public Result<Account> update(@RequestBody Account account) {
        accountService.updateById(account);
        return Result.success(account);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        accountService.removeById(id);
        return Result.success();
    }

    @GetMapping("/statistics/category")
    public Result<List<Map<String, Object>>> categoryStatistics(@RequestParam Long userId, @RequestParam String month, @RequestParam String type) {
        return Result.success(accountService.getCategoryStatistics(userId, month, type));
    }

    @GetMapping("/statistics/trend")
    public Result<List<Map<String, Object>>> trendStatistics(@RequestParam Long userId, @RequestParam String year) {
        return Result.success(accountService.getTrendStatistics(userId, year));
    }

    @GetMapping("/statistics/yearly")
    public Result<List<Map<String, Object>>> yearlyStatistics(@RequestParam Long userId, @RequestParam String year) {
        return Result.success(accountService.getYearlyStatistics(userId, year));
    }
}

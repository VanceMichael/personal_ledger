package com.personal.ledger.controller;

import com.personal.ledger.common.Result;
import com.personal.ledger.dto.TransactionDTO;
import com.personal.ledger.dto.TransactionQueryDTO;
import com.personal.ledger.entity.Transaction;
import com.personal.ledger.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/list")
    public Result<List<Transaction>> list(TransactionQueryDTO dto) {
        try {
            List<Transaction> list = transactionService.listWithCategory(dto);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody TransactionDTO dto) {
        try {
            transactionService.addTransaction(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody TransactionDTO dto) {
        try {
            transactionService.updateTransaction(id, dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

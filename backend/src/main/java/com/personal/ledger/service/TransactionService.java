package com.personal.ledger.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personal.ledger.dto.TransactionDTO;
import com.personal.ledger.dto.TransactionQueryDTO;
import com.personal.ledger.entity.Transaction;
import com.personal.ledger.mapper.TransactionMapper;
import com.personal.ledger.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TransactionService extends ServiceImpl<TransactionMapper, Transaction> {
    @Autowired
    private TransactionMapper transactionMapper;

    public List<Transaction> listWithCategory(TransactionQueryDTO dto) {
        Long userId = UserContext.getUserId();
        return transactionMapper.selectListWithCategory(userId, dto.getStartDate(),
                dto.getEndDate(), dto.getType(), dto.getCategoryId());
    }

    public void addTransaction(TransactionDTO dto) {
        Long userId = UserContext.getUserId();
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setType(dto.getType());
        transaction.setCategoryId(dto.getCategoryId());
        transaction.setAmount(dto.getAmount());
        transaction.setRemark(dto.getRemark());
        transaction.setTransactionDate(dto.getTransactionDate());
        save(transaction);
    }

    public void updateTransaction(Long id, TransactionDTO dto) {
        Long userId = UserContext.getUserId();
        Transaction transaction = getById(id);
        if (transaction == null || !transaction.getUserId().equals(userId)) {
            throw new RuntimeException("账目不存在或无权限修改");
        }
        transaction.setType(dto.getType());
        transaction.setCategoryId(dto.getCategoryId());
        transaction.setAmount(dto.getAmount());
        transaction.setRemark(dto.getRemark());
        transaction.setTransactionDate(dto.getTransactionDate());
        updateById(transaction);
    }

    public void deleteTransaction(Long id) {
        Long userId = UserContext.getUserId();
        Transaction transaction = getById(id);
        if (transaction == null || !transaction.getUserId().equals(userId)) {
            throw new RuntimeException("账目不存在或无权限删除");
        }
        removeById(id);
    }
}

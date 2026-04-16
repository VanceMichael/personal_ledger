package com.ledger.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.dto.AccountDTO;
import com.ledger.entity.Account;
import com.ledger.mapper.AccountMapper;
import com.ledger.service.AccountService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Override
    public List<Account> listWithCategory(AccountDTO dto) {
        return this.baseMapper.listWithCategory(dto.getUserId(), dto.getStartDate(), dto.getEndDate(), dto.getType(), dto.getCategoryId());
    }

    @Override
    public List<Map<String, Object>> getCategoryStatistics(Long userId, String month, String type) {
        return this.baseMapper.getCategoryStatistics(userId, month, type);
    }

    @Override
    public List<Map<String, Object>> getTrendStatistics(Long userId, String year) {
        return this.baseMapper.getTrendStatistics(userId, year);
    }

    @Override
    public List<Map<String, Object>> getYearlyStatistics(Long userId, String year) {
        return this.baseMapper.getYearlyStatistics(userId, year);
    }
}

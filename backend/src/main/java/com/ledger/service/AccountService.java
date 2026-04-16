package com.ledger.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.dto.AccountDTO;
import com.ledger.entity.Account;
import java.util.List;
import java.util.Map;

public interface AccountService extends IService<Account> {
    List<Account> listWithCategory(AccountDTO dto);

    List<Map<String, Object>> getCategoryStatistics(Long userId, String month, String type);

    List<Map<String, Object>> getTrendStatistics(Long userId, String year);

    List<Map<String, Object>> getYearlyStatistics(Long userId, String year);
}

package com.ledger.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.entity.Budget;
import com.ledger.mapper.BudgetMapper;
import com.ledger.service.BudgetService;
import org.springframework.stereotype.Service;

@Service
public class BudgetServiceImpl extends ServiceImpl<BudgetMapper, Budget> implements BudgetService {

    @Override
    public Budget getByMonth(Long userId, String month) {
        LambdaQueryWrapper<Budget> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Budget::getUserId, userId).eq(Budget::getMonth, month);
        return this.getOne(wrapper);
    }
}

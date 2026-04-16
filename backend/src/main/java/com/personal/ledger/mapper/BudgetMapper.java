package com.personal.ledger.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.ledger.entity.Budget;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BudgetMapper extends BaseMapper<Budget> {
}

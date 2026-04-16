package com.personal.ledger.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.ledger.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface TransactionMapper extends BaseMapper<Transaction> {
    List<Transaction> selectListWithCategory(@Param("userId") Long userId,
                                              @Param("startDate") String startDate,
                                              @Param("endDate") String endDate,
                                              @Param("type") Integer type,
                                              @Param("categoryId") Long categoryId);

    BigDecimal selectTotalExpense(@Param("userId") Long userId,
                                  @Param("year") Integer year,
                                  @Param("month") Integer month);

    List<Map<String, Object>> selectCategoryStat(@Param("userId") Long userId,
                                                 @Param("type") Integer type,
                                                 @Param("startDate") String startDate,
                                                 @Param("endDate") String endDate);

    List<Map<String, Object>> selectTrendStat(@Param("userId") Long userId,
                                              @Param("startDate") String startDate,
                                              @Param("endDate") String endDate);
}

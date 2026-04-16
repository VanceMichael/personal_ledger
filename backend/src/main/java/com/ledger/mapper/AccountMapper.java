package com.ledger.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ledger.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
    List<Account> listWithCategory(@Param("userId") Long userId, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("type") String type, @Param("categoryId") Long categoryId);

    List<Map<String, Object>> getCategoryStatistics(@Param("userId") Long userId, @Param("month") String month, @Param("type") String type);

    List<Map<String, Object>> getTrendStatistics(@Param("userId") Long userId, @Param("year") String year);

    List<Map<String, Object>> getYearlyStatistics(@Param("userId") Long userId, @Param("year") String year);
}

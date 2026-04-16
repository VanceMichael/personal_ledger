package com.personal.ledger.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.ledger.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}

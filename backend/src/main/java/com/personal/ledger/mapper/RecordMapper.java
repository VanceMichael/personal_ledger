package com.personal.ledger.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.personal.ledger.entity.Record;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecordMapper extends BaseMapper<Record> {
}

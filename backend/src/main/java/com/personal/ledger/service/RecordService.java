package com.personal.ledger.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.personal.ledger.dto.RecordDTO;
import com.personal.ledger.entity.Record;
import com.personal.ledger.vo.RecordVO;
import java.time.LocalDateTime;
import java.util.List;

public interface RecordService extends IService<Record> {
    Record addRecord(RecordDTO dto);
    Record updateRecord(Long id, RecordDTO dto);
    void deleteRecord(Long id);
    Page<RecordVO> getRecordPage(Long userId, Integer type, Long categoryId, LocalDateTime startTime, LocalDateTime endTime, Integer pageNum, Integer pageSize);
    List<RecordVO> getRecordList(Long userId, Integer type, Long categoryId, LocalDateTime startTime, LocalDateTime endTime);
}

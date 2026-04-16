package com.personal.ledger.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.personal.ledger.dto.RecordDTO;
import com.personal.ledger.entity.Record;
import com.personal.ledger.service.RecordService;
import com.personal.ledger.utils.Result;
import com.personal.ledger.vo.RecordVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/record")
@CrossOrigin
public class RecordController {

    @Resource
    private RecordService recordService;

    @PostMapping
    public Result<Record> addRecord(@Valid @RequestBody RecordDTO dto) {
        try {
            Record record = recordService.addRecord(dto);
            return Result.success(record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Record> updateRecord(@PathVariable Long id, @Valid @RequestBody RecordDTO dto) {
        try {
            Record record = recordService.updateRecord(id, dto);
            return Result.success(record);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRecord(@PathVariable Long id) {
        try {
            recordService.deleteRecord(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/page")
    public Result<Page<RecordVO>> getRecordPage(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<RecordVO> page = recordService.getRecordPage(userId, type, categoryId, startTime, endTime, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/list")
    public Result<List<RecordVO>> getRecordList(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) LocalDateTime startTime,
            @RequestParam(required = false) LocalDateTime endTime) {
        List<RecordVO> list = recordService.getRecordList(userId, type, categoryId, startTime, endTime);
        return Result.success(list);
    }
}

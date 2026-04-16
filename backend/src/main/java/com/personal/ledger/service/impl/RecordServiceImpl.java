package com.personal.ledger.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personal.ledger.dto.RecordDTO;
import com.personal.ledger.entity.Category;
import com.personal.ledger.entity.Record;
import com.personal.ledger.mapper.RecordMapper;
import com.personal.ledger.service.CategoryService;
import com.personal.ledger.service.RecordService;
import com.personal.ledger.vo.RecordVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RecordServiceImpl extends ServiceImpl<RecordMapper, Record> implements RecordService {

    @Resource
    private CategoryService categoryService;

    @Override
    public Record addRecord(RecordDTO dto) {
        Record record = new Record();
        BeanUtils.copyProperties(dto, record);
        save(record);
        return record;
    }

    @Override
    public Record updateRecord(Long id, RecordDTO dto) {
        Record record = getById(id);
        if (record == null) {
            throw new RuntimeException("记录不存在");
        }
        BeanUtils.copyProperties(dto, record);
        updateById(record);
        return record;
    }

    @Override
    public void deleteRecord(Long id) {
        removeById(id);
    }

    @Override
    public Page<RecordVO> getRecordPage(Long userId, Integer type, Long categoryId, LocalDateTime startTime, LocalDateTime endTime, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<Record> queryWrapper = buildQueryWrapper(userId, type, categoryId, startTime, endTime);
        queryWrapper.orderByDesc(Record::getRecordTime);
        Page<Record> recordPage = page(new Page<>(pageNum, pageSize), queryWrapper);
        return convertToVOPage(recordPage);
    }

    @Override
    public List<RecordVO> getRecordList(Long userId, Integer type, Long categoryId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<Record> queryWrapper = buildQueryWrapper(userId, type, categoryId, startTime, endTime);
        queryWrapper.orderByDesc(Record::getRecordTime);
        List<Record> records = list(queryWrapper);
        return convertToVOList(records);
    }

    private LambdaQueryWrapper<Record> buildQueryWrapper(Long userId, Integer type, Long categoryId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<Record> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Record::getUserId, userId);
        if (type != null) {
            queryWrapper.eq(Record::getType, type);
        }
        if (categoryId != null) {
            queryWrapper.eq(Record::getCategoryId, categoryId);
        }
        if (startTime != null) {
            queryWrapper.ge(Record::getRecordTime, startTime);
        }
        if (endTime != null) {
            queryWrapper.le(Record::getRecordTime, endTime);
        }
        return queryWrapper;
    }

    private Page<RecordVO> convertToVOPage(Page<Record> recordPage) {
        Page<RecordVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        List<RecordVO> voList = convertToVOList(recordPage.getRecords());
        voPage.setRecords(voList);
        return voPage;
    }

    private List<RecordVO> convertToVOList(List<Record> records) {
        if (records.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> categoryIds = records.stream().map(Record::getCategoryId).distinct().toList();
        List<Category> categories = categoryService.listByIds(categoryIds);
        Map<Long, Category> categoryMap = categories.stream().collect(Collectors.toMap(Category::getId, c -> c));
        
        return records.stream().map(record -> {
            RecordVO vo = new RecordVO();
            BeanUtils.copyProperties(record, vo);
            Category category = categoryMap.get(record.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
                vo.setCategoryIcon(category.getIcon());
            }
            return vo;
        }).toList();
    }
}

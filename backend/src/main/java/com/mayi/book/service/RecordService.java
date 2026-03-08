package com.mayi.book.service;

import com.mayi.book.dto.*;
import com.mayi.book.entity.Record;
import com.mayi.book.mapper.RecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecordService {
    
    @Autowired
    private RecordMapper recordMapper;
    
    @Autowired
    private CategoryService categoryService;

    @Transactional
    public RecordDTO addRecord(Long userId, RecordAddRequest request) {
        // 验证分类是否存在
        com.mayi.book.entity.Category category = categoryService.getCategoryById(request.getCategoryId());
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 创建记账记录
        Record record = new Record();
        record.setUserId(userId);
        record.setCategoryId(request.getCategoryId());
        record.setType(request.getType());
        record.setAmount(request.getAmount());
        record.setRemark(request.getRemark());
        record.setRecordDate(LocalDate.now());
        
        recordMapper.insert(record);
        
        // 返回DTO
        RecordDTO dto = new RecordDTO();
        dto.setId(record.getId());
        dto.setCategoryName(category.getName());
        dto.setAmount(record.getAmount());
        dto.setRemark(record.getRemark());
        dto.setCreateTime(record.getCreateTime());
        
        return dto;
    }

    public RecordListResponse getRecordList(Long userId, RecordListRequest request) {
        // 计算偏移量
        Integer offset = (request.getPage() - 1) * request.getPageSize();
        
        // 查询分组数据（按天分组，包含汇总信息）
        List<RecordGroupDTO> groupList = recordMapper.selectRecordGroupList(
                userId, request.getMonth(), request.getType(), offset, request.getPageSize());
        
        // 查询总数
        Long total = recordMapper.countRecordGroup(userId, request.getMonth(), request.getType());
        
        // 为每个日期组查询详细的记账记录
        for (RecordGroupDTO group : groupList) {
            List<RecordDTO> records = recordMapper.selectRecordsByDate(userId, group.getDate(), request.getType());
            group.setRecords(records);
        }
        
        RecordListResponse response = new RecordListResponse();
        response.setTotal(total);
        response.setList(groupList);
        
        return response;
    }

    public StatisticsResponse getStatistics(Long userId, String month) {
        return recordMapper.selectStatistics(userId, month);
    }
}

package com.mayi.book.controller;

import com.mayi.book.dto.*;
import com.mayi.book.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/record")
@CrossOrigin
public class RecordController {
    
    @Autowired
    private RecordService recordService;

    @PostMapping("/add")
    public ApiResponse<RecordDTO> addRecord(@RequestBody RecordAddRequest request, HttpServletRequest httpRequest) {
        try {
            Long userId = (Long) httpRequest.getAttribute("userId");
            if (userId == null) {
                return ApiResponse.error(401, "未授权");
            }
            RecordDTO record = recordService.addRecord(userId, request);
            return ApiResponse.success("保存成功", record);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ApiResponse<RecordListResponse> getRecordList(@RequestParam(required = false) Integer page,
                                                         @RequestParam(required = false) Integer pageSize,
                                                         @RequestParam(required = false) String month,
                                                         @RequestParam(required = false) Integer type,
                                                         HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ApiResponse.error(401, "未授权");
            }
            
            RecordListRequest listRequest = new RecordListRequest();
            if (page != null) listRequest.setPage(page);
            if (pageSize != null) listRequest.setPageSize(pageSize);
            if (month != null) listRequest.setMonth(month);
            if (type != null) listRequest.setType(type);
            
            RecordListResponse response = recordService.getRecordList(userId, listRequest);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public ApiResponse<StatisticsResponse> getStatistics(@RequestParam(required = false) String month,
                                                          HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ApiResponse.error(401, "未授权");
            }
            StatisticsResponse response = recordService.getStatistics(userId, month);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 按周统计（前端通常传入 startDate / endDate，格式：yyyy-MM-dd）
     */
    @GetMapping("/weekly-statistics")
    public ApiResponse<WeeklyStatisticsResponse> getWeeklyStatistics(@RequestParam(required = false) String startDate,
                                                                     @RequestParam(required = false) String endDate,
                                                                     @RequestParam(required = false) Integer type,
                                                                     HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ApiResponse.error(401, "未授权");
            }
            java.time.LocalDate start = null;
            java.time.LocalDate end = null;
            if (startDate != null && !startDate.isEmpty()) {
                start = java.time.LocalDate.parse(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = java.time.LocalDate.parse(endDate);
            }
            WeeklyStatisticsResponse response = recordService.getWeeklyStatistics(userId, start, end, type);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 支出排行榜：按分类支出从高到低，需传 startDate、endDate（yyyy-MM-dd）
     */
    @GetMapping("/expense-ranking")
    public ApiResponse<java.util.List<CategoryExpenseRankDTO>> getExpenseRanking(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ApiResponse.error(401, "未授权");
            }
            java.time.LocalDate start = null;
            java.time.LocalDate end = null;
            if (startDate != null && !startDate.isEmpty()) {
                start = java.time.LocalDate.parse(startDate);
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = java.time.LocalDate.parse(endDate);
            }
            java.util.List<CategoryExpenseRankDTO> list = recordService.getExpenseRanking(userId, start, end);
            return ApiResponse.success(list);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 按年统计（月维度折线、总金额、月均），参数 year、type
     */
    @GetMapping("/year-statistics")
    public ApiResponse<YearStatisticsResponse> getYearStatistics(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer type,
            HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ApiResponse.error(401, "未授权");
            }
            YearStatisticsResponse response = recordService.getYearStatistics(userId, year, type);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

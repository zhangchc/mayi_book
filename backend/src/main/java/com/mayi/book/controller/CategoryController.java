package com.mayi.book.controller;

import com.mayi.book.dto.ApiResponse;
import com.mayi.book.entity.Category;
import com.mayi.book.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@CrossOrigin
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/expense")
    public ApiResponse<List<Category>> getExpenseCategories() {
        try {
            List<Category> categories = categoryService.getExpenseCategories();
            return ApiResponse.success(categories);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @GetMapping("/income")
    public ApiResponse<List<Category>> getIncomeCategories() {
        try {
            List<Category> categories = categoryService.getIncomeCategories();
            return ApiResponse.success(categories);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}

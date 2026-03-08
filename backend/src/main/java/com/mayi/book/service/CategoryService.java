package com.mayi.book.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mayi.book.entity.Category;
import com.mayi.book.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> getExpenseCategories() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getType, "expense")
                    .eq(Category::getStatus, 1)
                    .orderByAsc(Category::getSortOrder);
        return categoryMapper.selectList(queryWrapper);
    }

    public List<Category> getIncomeCategories() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getType, "income")
                    .eq(Category::getStatus, 1)
                    .orderByAsc(Category::getSortOrder);
        return categoryMapper.selectList(queryWrapper);
    }

    public Category getCategoryById(Long id) {
        return categoryMapper.selectById(id);
    }
}

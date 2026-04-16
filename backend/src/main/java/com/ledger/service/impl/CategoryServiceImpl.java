package com.ledger.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ledger.entity.Category;
import com.ledger.mapper.CategoryMapper;
import com.ledger.service.CategoryService;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> listByUserId(Long userId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getUserId, userId).orderByAsc(Category::getSort);
        return this.list(wrapper);
    }

    @Override
    public void initDefaultCategories(Long userId) {
        List<Category> defaultCategories = Arrays.asList(
            createCategory(userId, "餐饮", "expense", "🍔", 1),
            createCategory(userId, "交通", "expense", "🚗", 2),
            createCategory(userId, "购物", "expense", "🛒", 3),
            createCategory(userId, "娱乐", "expense", "🎮", 4),
            createCategory(userId, "医疗", "expense", "💊", 5),
            createCategory(userId, "教育", "expense", "📚", 6),
            createCategory(userId, "居住", "expense", "🏠", 7),
            createCategory(userId, "通讯", "expense", "📱", 8),
            createCategory(userId, "工资", "income", "💰", 1),
            createCategory(userId, "理财", "income", "📈", 2),
            createCategory(userId, "兼职", "income", "💼", 3),
            createCategory(userId, "奖金", "income", "🎁", 4),
            createCategory(userId, "其他", "income", "📦", 5)
        );
        this.saveBatch(defaultCategories);
    }

    private Category createCategory(Long userId, String name, String type, String icon, int sort) {
        Category category = new Category();
        category.setUserId(userId);
        category.setName(name);
        category.setType(type);
        category.setIcon(icon);
        category.setSort(sort);
        return category;
    }
}

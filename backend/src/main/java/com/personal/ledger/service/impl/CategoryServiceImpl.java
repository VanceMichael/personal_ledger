package com.personal.ledger.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personal.ledger.dto.CategoryDTO;
import com.personal.ledger.entity.Category;
import com.personal.ledger.mapper.CategoryMapper;
import com.personal.ledger.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> getCategoryList(Long userId, Integer type) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.eq(Category::getUserId, 0).or().eq(Category::getUserId, userId));
        if (type != null) {
            queryWrapper.eq(Category::getType, type);
        }
        queryWrapper.orderByAsc(Category::getSort).orderByAsc(Category::getId);
        return list(queryWrapper);
    }

    @Override
    public Category addCategory(CategoryDTO dto) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getUserId, dto.getUserId())
                .eq(Category::getName, dto.getName())
                .eq(Category::getType, dto.getType());
        Category existCategory = getOne(queryWrapper);
        if (existCategory != null) {
            throw new RuntimeException("该分类已存在");
        }

        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        if (category.getSort() == null) {
            category.setSort(99);
        }
        save(category);
        return category;
    }

    @Override
    public Category updateCategory(Long id, CategoryDTO dto) {
        Category category = getById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        if (category.getUserId() == 0) {
            throw new RuntimeException("系统分类无法修改");
        }
        if (!category.getUserId().equals(dto.getUserId())) {
            throw new RuntimeException("无权限修改该分类");
        }

        BeanUtils.copyProperties(dto, category, "userId");
        updateById(category);
        return category;
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = getById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        if (category.getUserId() == 0) {
            throw new RuntimeException("系统分类无法删除");
        }
        removeById(id);
    }
}

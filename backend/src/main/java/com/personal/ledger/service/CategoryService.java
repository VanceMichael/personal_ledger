package com.personal.ledger.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personal.ledger.dto.CategoryDTO;
import com.personal.ledger.entity.Category;
import com.personal.ledger.mapper.CategoryMapper;
import com.personal.ledger.util.UserContext;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {
    public List<Category> listByType(Integer type) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.isNull(Category::getUserId).or().eq(Category::getUserId, userId));
        if (type != null) {
            wrapper.eq(Category::getType, type);
        }
        wrapper.orderByAsc(Category::getSort).orderByAsc(Category::getId);
        return list(wrapper);
    }

    public void addCategory(CategoryDTO dto) {
        Long userId = UserContext.getUserId();
        Category category = new Category();
        category.setUserId(userId);
        category.setName(dto.getName());
        category.setType(dto.getType());
        category.setIcon(dto.getIcon());
        category.setSort(dto.getSort() != null ? dto.getSort() : 0);
        save(category);
    }

    public void updateCategory(Long id, CategoryDTO dto) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getId, id).eq(Category::getUserId, userId);
        Category category = getOne(wrapper);
        if (category == null) {
            throw new RuntimeException("分类不存在或无权限修改");
        }
        category.setName(dto.getName());
        category.setIcon(dto.getIcon());
        category.setSort(dto.getSort());
        updateById(category);
    }

    public void deleteCategory(Long id) {
        Long userId = UserContext.getUserId();
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getId, id).eq(Category::getUserId, userId);
        Category category = getOne(wrapper);
        if (category == null) {
            throw new RuntimeException("分类不存在或无权限删除");
        }
        removeById(id);
    }
}

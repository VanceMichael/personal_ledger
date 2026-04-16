package com.personal.ledger.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.personal.ledger.dto.CategoryDTO;
import com.personal.ledger.entity.Category;
import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> getCategoryList(Long userId, Integer type);
    Category addCategory(CategoryDTO dto);
    Category updateCategory(Long id, CategoryDTO dto);
    void deleteCategory(Long id);
}

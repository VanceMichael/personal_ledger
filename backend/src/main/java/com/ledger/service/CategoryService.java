package com.ledger.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.entity.Category;
import java.util.List;

public interface CategoryService extends IService<Category> {
    List<Category> listByUserId(Long userId);

    void initDefaultCategories(Long userId);
}

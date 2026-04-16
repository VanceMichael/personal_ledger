package com.ledger.controller;

import com.ledger.common.Result;
import com.ledger.entity.Category;
import com.ledger.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list/{userId}")
    public Result<List<Category>> list(@PathVariable Long userId) {
        return Result.success(categoryService.listByUserId(userId));
    }

    @PostMapping("/add")
    public Result<Category> add(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success(category);
    }

    @PutMapping("/update")
    public Result<Category> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success(category);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success();
    }
}

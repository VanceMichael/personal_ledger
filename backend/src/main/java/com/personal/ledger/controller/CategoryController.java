package com.personal.ledger.controller;

import com.personal.ledger.common.Result;
import com.personal.ledger.dto.CategoryDTO;
import com.personal.ledger.entity.Category;
import com.personal.ledger.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> list(@RequestParam(required = false) Integer type) {
        try {
            List<Category> list = categoryService.listByType(type);
            return Result.success(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result<Void> add(@Valid @RequestBody CategoryDTO dto) {
        try {
            categoryService.addCategory(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {
        try {
            categoryService.updateCategory(id, dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

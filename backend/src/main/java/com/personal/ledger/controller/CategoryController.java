package com.personal.ledger.controller;

import com.personal.ledger.dto.CategoryDTO;
import com.personal.ledger.entity.Category;
import com.personal.ledger.service.CategoryService;
import com.personal.ledger.utils.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/category")
@CrossOrigin
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> getCategoryList(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer type) {
        List<Category> list = categoryService.getCategoryList(userId, type);
        return Result.success(list);
    }

    @PostMapping
    public Result<Category> addCategory(@Valid @RequestBody CategoryDTO dto) {
        try {
            Category category = categoryService.addCategory(dto);
            return Result.success(category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<Category> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO dto) {
        try {
            Category category = categoryService.updateCategory(id, dto);
            return Result.success(category);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

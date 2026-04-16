package com.ledger.controller;

import com.ledger.common.Result;
import com.ledger.dto.LoginDTO;
import com.ledger.entity.User;
import com.ledger.service.CategoryService;
import com.ledger.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginDTO dto) {
        User user = userService.login(dto.getUsername(), dto.getPassword());
        if (user != null) {
            user.setPassword(null);
            return Result.success(user);
        }
        return Result.error("用户名或密码错误");
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        User newUser = userService.register(user);
        if (newUser != null) {
            categoryService.initDefaultCategories(newUser.getId());
            newUser.setPassword(null);
            return Result.success(newUser);
        }
        return Result.error("用户名已存在");
    }

    @PutMapping("/update")
    public Result<User> update(@RequestBody User user) {
        userService.updateById(user);
        return Result.success(user);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
}

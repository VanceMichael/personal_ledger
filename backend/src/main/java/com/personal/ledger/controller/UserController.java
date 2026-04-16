package com.personal.ledger.controller;

import com.personal.ledger.dto.UserLoginDTO;
import com.personal.ledger.dto.UserRegisterDTO;
import com.personal.ledger.dto.UserUpdateDTO;
import com.personal.ledger.entity.User;
import com.personal.ledger.service.UserService;
import com.personal.ledger.utils.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Result<User> register(@Valid @RequestBody UserRegisterDTO dto) {
        try {
            User user = userService.register(dto);
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public Result<User> login(@Valid @RequestBody UserLoginDTO dto) {
        try {
            User user = userService.login(dto);
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{userId}")
    public Result<User> updateInfo(@PathVariable Long userId, @RequestBody UserUpdateDTO dto) {
        try {
            User user = userService.updateInfo(userId, dto);
            user.setPassword(null);
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    public Result<User> getInfo(@PathVariable Long userId) {
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }
}

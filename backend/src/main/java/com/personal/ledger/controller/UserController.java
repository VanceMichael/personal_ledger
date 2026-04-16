package com.personal.ledger.controller;

import com.personal.ledger.common.Result;
import com.personal.ledger.dto.UserLoginDTO;
import com.personal.ledger.dto.UserRegisterDTO;
import com.personal.ledger.dto.UserUpdateDTO;
import com.personal.ledger.entity.User;
import com.personal.ledger.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody UserLoginDTO dto) {
        try {
            Map<String, Object> result = userService.login(dto);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterDTO dto) {
        try {
            userService.register(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    public Result<User> getUserInfo() {
        try {
            User user = userService.getUserInfo();
            return Result.success(user);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<Void> updateUserInfo(@RequestBody UserUpdateDTO dto) {
        try {
            userService.updateUserInfo(dto);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

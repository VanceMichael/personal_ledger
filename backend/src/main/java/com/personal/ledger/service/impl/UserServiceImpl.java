package com.personal.ledger.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personal.ledger.dto.UserLoginDTO;
import com.personal.ledger.dto.UserRegisterDTO;
import com.personal.ledger.dto.UserUpdateDTO;
import com.personal.ledger.entity.User;
import com.personal.ledger.mapper.UserMapper;
import com.personal.ledger.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User register(UserRegisterDTO dto) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, dto.getUsername());
        User existUser = getOne(queryWrapper);
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(DigestUtil.md5Hex(dto.getPassword()));
        user.setNickname(StrUtil.isNotBlank(dto.getNickname()) ? dto.getNickname() : dto.getUsername());
        save(user);
        return user;
    }

    @Override
    public User login(UserLoginDTO dto) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, dto.getUsername());
        User user = getOne(queryWrapper);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        if (!DigestUtil.md5Hex(dto.getPassword()).equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        return user;
    }

    @Override
    public User updateInfo(Long userId, UserUpdateDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (StrUtil.isNotBlank(dto.getNickname())) {
            user.setNickname(dto.getNickname());
        }
        if (StrUtil.isNotBlank(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }
        if (StrUtil.isNotBlank(dto.getEmail())) {
            user.setEmail(dto.getEmail());
        }
        if (StrUtil.isNotBlank(dto.getPassword())) {
            user.setPassword(DigestUtil.md5Hex(dto.getPassword()));
        }
        updateById(user);
        return user;
    }
}

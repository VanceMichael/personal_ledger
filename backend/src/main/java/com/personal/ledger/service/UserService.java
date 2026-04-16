package com.personal.ledger.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.personal.ledger.dto.UserLoginDTO;
import com.personal.ledger.dto.UserRegisterDTO;
import com.personal.ledger.dto.UserUpdateDTO;
import com.personal.ledger.entity.User;
import com.personal.ledger.mapper.UserMapper;
import com.personal.ledger.common.BusinessException;
import com.personal.ledger.util.JwtUtil;
import com.personal.ledger.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    @Autowired
    private JwtUtil jwtUtil;

    public Map<String, Object> login(UserLoginDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        User user = getOne(wrapper);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        String password = DigestUtils.md5DigestAsHex(dto.getPassword().getBytes());
        if (!password.equals(user.getPassword())) {
            throw new BusinessException("密码错误");
        }
        String token = jwtUtil.generateToken(user.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userInfo", user);
        user.setPassword(null);
        return result;
    }

    public void register(UserRegisterDTO dto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, dto.getUsername());
        if (count(wrapper) > 0) {
            throw new BusinessException("用户名已存在");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(DigestUtils.md5DigestAsHex(dto.getPassword().getBytes()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        save(user);
    }

    public User getUserInfo() {
        Long userId = UserContext.getUserId();
        User user = getById(userId);
        user.setPassword(null);
        return user;
    }

    public void updateUserInfo(UserUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        User user = new User();
        user.setId(userId);
        user.setNickname(dto.getNickname());
        user.setAvatar(dto.getAvatar());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        updateById(user);
    }

    public void updatePassword(String oldPassword, String newPassword) {
        Long userId = UserContext.getUserId();
        User user = getById(userId);
        String oldPwdMd5 = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!oldPwdMd5.equals(user.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        updateById(user);
    }
}

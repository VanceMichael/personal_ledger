package com.personal.ledger.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.personal.ledger.entity.User;
import com.personal.ledger.dto.*;

public interface UserService extends IService<User> {
    User register(UserRegisterDTO dto);
    User login(UserLoginDTO dto);
    User updateInfo(Long userId, UserUpdateDTO dto);
}

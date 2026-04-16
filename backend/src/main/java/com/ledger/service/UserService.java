package com.ledger.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ledger.entity.User;

public interface UserService extends IService<User> {
    User login(String username, String password);

    User register(User user);
}

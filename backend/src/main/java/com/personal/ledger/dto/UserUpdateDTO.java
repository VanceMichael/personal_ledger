package com.personal.ledger.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private String nickname;
    private String avatar;
    private String email;
    private String password;
}

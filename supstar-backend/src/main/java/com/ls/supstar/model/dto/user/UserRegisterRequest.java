package com.ls.supstar.model.dto.user;

import lombok.Data;

@Data
public class UserRegisterRequest {



    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;


    /**
     * 校验密码
     */
    private String checkPassword;


}

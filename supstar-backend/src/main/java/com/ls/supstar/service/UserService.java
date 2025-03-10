package com.ls.supstar.service;

import com.ls.supstar.model.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ls.supstar.model.vo.LoginUserVo;

import javax.servlet.http.HttpServletRequest;

/**
* @author 26611
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2025-03-09 15:55:40
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long register(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @return
     */
    LoginUserVo login(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户登出
     * @param request
     * @return
     */
    Boolean logout(HttpServletRequest request);

    /**
     * 获取登录用户
     * @param request
     * @return
     */
    LoginUserVo getLogin(HttpServletRequest request);
}

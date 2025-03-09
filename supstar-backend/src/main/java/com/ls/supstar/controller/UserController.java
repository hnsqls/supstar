package com.ls.supstar.controller;

import com.ls.supstar.common.BaseResponse;
import com.ls.supstar.common.ErrorCode;
import com.ls.supstar.common.ResultUtils;
import com.ls.supstar.exeception.BusinessException;
import com.ls.supstar.model.dto.user.UserRegisterRequest;

import com.ls.supstar.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {

        //逻辑校验参数
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"注册参数错误");

        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"注册参数错误");
        }

        //用户注册
        long  result = userService.register(userAccount,userPassword,checkPassword);

        return ResultUtils.success(result);
    }



}

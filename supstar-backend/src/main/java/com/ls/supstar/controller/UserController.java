package com.ls.supstar.controller;

import com.ls.supstar.common.BaseResponse;
import com.ls.supstar.common.ErrorCode;
import com.ls.supstar.common.ResultUtils;
import com.ls.supstar.exeception.BusinessException;
import com.ls.supstar.model.dto.user.UserLoginRequest;
import com.ls.supstar.model.dto.user.UserRegisterRequest;

import com.ls.supstar.model.entity.User;
import com.ls.supstar.model.vo.LoginUserVo;
import com.ls.supstar.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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

    /**
     * 用户登录
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVo> register(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {

        //逻辑校验参数
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        //用户登录
        LoginUserVo  loginUserVo = userService.login(userAccount,userPassword,request);

        return ResultUtils.success(loginUserVo);
    }

    /**
     * 用户退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> register(HttpServletRequest request) {

        //逻辑校验参数
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        //用户退出
        Boolean result = userService.logout(request);
        return ResultUtils.success(result);
    }


    /**
     * 获取登录用户
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVo> getLoginUserVo(HttpServletRequest request) {

        LoginUserVo loginUserVo = userService.getLogin(request);
        return ResultUtils.success(loginUserVo);

    }



}

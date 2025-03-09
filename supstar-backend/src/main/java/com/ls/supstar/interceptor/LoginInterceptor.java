package com.ls.supstar.interceptor;


import com.ls.supstar.model.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//@Component  拦截器是非常轻量级的组件，只有再需要时才会被调用

/**
 * 拦截器，拦截需要认证的业务
 */
public class LoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //  session登录方式
//        //1. 获取session
        HttpSession session = request.getSession();
//        // 2. 获取session中的用户
        Object user = session.getAttribute("user");
        if (user == null ) {
            //没有用户信息
            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"code\": 401, \"message\": \"用户未登录\"}");
            return false;
        }

//        //3. todo 保存到ThreadLocal中
//        UserHolder.saveUser((User) user);

        return true;

    }

    // todo ThreadLocal
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//       //移除信息，避免内存泄露
//        UserHolder.removeUser();
//    }
}

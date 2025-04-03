package com.ls.supstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * 测试拦截器的执行顺序
 */
@RestController
public class DebugController {

    @Autowired
    private List<AbstractHandlerMapping> handlerMappings; // 注入所有HandlerMapping

    @GetMapping("/debug/interceptors")
    public List<String> listInterceptors(HttpServletRequest request) throws Exception {
        List<String> interceptors = new ArrayList<>();

        for (AbstractHandlerMapping handlerMapping : handlerMappings) {
            HandlerExecutionChain handlerChain = handlerMapping.getHandler(request);
            if (handlerChain != null) {
                handlerChain.getInterceptorList().forEach(interceptor -> {
                    interceptors.add(interceptor.getClass().getName());
                });
            }
        }

        return interceptors;
    }
}
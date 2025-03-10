package com.ls.supstar.aop;


import com.ls.supstar.exeception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.UUID;

/**
 * 日志 AOP
 */
@Component
@Aspect
@Slf4j
public class LogInterceptor {


    @Around("execution(* com.ls.supstar.controller.*.*(..))")
    public Object doInterceptor(ProceedingJoinPoint point) throws Throwable {

        // 记时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 获取请求的路径
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 生成请求日志唯一id
        String requestId = UUID.randomUUID().toString();

        // 请求的ip
        String requestIp = request.getRemoteAddr();

//        // 请求的主机名称
//        String clientHostName = getClientHostName(requestIp);


        // 发起请求的用户
        Object user = request.getSession().getAttribute("user");

        // 请求url
        String url = request.getRequestURL().toString();



        // todo 请求参数, 注意隐私，特别是密码要进行加密
//        Object[] args = point.getArgs();
//
//        String reqParam = "[" + StringUtils.join(args, ",") + "]";

        // 输出请求日志
//        log.info("request start: requestId:{}, userId:{},requestIp:{},url:{}, reqParam:{}", requestId,user,requestIp, url, reqParam);
        log.info("request start: requestId:{}, userId:{},requestIp:{},url:{}", requestId,user,requestIp, url);




        Object result = null;
        try {

            // 执行目标方法并获取响应结果
            result = point.proceed();

            // 将响应结果转换为字符串
            String respParam = result != null ? result.toString() : "error";

            // 输出响应日志
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();
            log.info("request end: requestId: {}, cost: {}ms, respParam: {}", requestId, totalTimeMillis, respParam);
        } catch (BusinessException e) {
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();
            log.error("request end Business exception caught: requestId: {}, cost: {}ms, code: {}, message: {}",
                    requestId, totalTimeMillis, e.getCode(), e.getMessage());
            throw e; // 保持 BusinessException 类型不变
        } catch (Exception e) {
            stopWatch.stop();
            long totalTimeMillis = stopWatch.getTotalTimeMillis();
            log.error("request end System exception caught: requestId: {}, cost: {}ms, message: {}",
                    requestId, totalTimeMillis, e.getMessage());
            throw e; // 保持 Exception 类型不变
        }
        return result;

    }


    /**
     * 获取客户端主机名
     * @param clientIp
     * @return
     */
    public static String getClientHostName(String clientIp) {
        try {
            InetAddress inetAddress = InetAddress.getByName(clientIp);
            return inetAddress.getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }






}

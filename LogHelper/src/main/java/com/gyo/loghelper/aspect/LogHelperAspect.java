package com.gyo.loghelper.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyo.loghelper.config.LogHelperProperties;
import com.gyo.loghelper.config.RabbitMQEnabledCondition;
import com.gyo.loghelper.entity.Log;
import com.gyo.loghelper.util.JwtParser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;


@Aspect
public class LogHelperAspect {
    private final MongoTemplate mongoTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final LogHelperProperties properties;
    private final ObjectProvider<HttpServletRequest> requestProvider;

    private final JwtParser jwtParser;  // 使用接口，而不是固定实现
    public LogHelperAspect(LogHelperProperties properties,MongoTemplate mongoTemplate,RabbitTemplate rabbitTemplate,
                           ObjectProvider<HttpServletRequest> requestProvider,JwtParser jwtParser) {
        this.properties = properties;
        this.mongoTemplate = mongoTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.requestProvider = requestProvider;
        this.jwtParser = jwtParser;
    }

    @Around("@annotation(com.gyo.loghelper.aspect.Loggable)")
    public Object logApiCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        if (!properties.isEnabled()) {
            return joinPoint.proceed();
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("No request context available");
        }
        HttpServletRequest request = attributes.getRequest();
        String path = request.getRequestURI();

        List<String> excludePaths = properties.getExcludePaths();
        if (excludePaths != null && excludePaths.contains(path)) {
            return joinPoint.proceed();
        }

        String token = request.getHeader("Authorization");
        Map<String, Object> userInfo = jwtParser.parseJwt(token, properties.getJwtSecret()); // 使用外部提供的解析器
        Map<String, Object> params = getRequestParams(request);
        String clientIp = getClientIp(request);
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Loggable loggable = method.getAnnotation(Loggable.class);

        Log log = new Log();
        if (loggable != null) {
            log.setDescription(loggable.value()); // 读取接口描述信息
        }
        log.setInterfaceName(path);
        log.setMethod(request.getMethod());
        log.setRequestParams(params);
        log.setToken(token);

        log.setUserInfo(userInfo);
        // 时区转换
        int timestamp = (int) (System.currentTimeMillis() / 1000);
        log.setCreateTime(timestamp);
        log.setClientIp(clientIp);

        Object result;
        try {
            result = joinPoint.proceed();
            long responseTime = System.currentTimeMillis() - startTime;
            log.setProcessTime(responseTime);
            log.setResponseResult(result.toString());
        } catch (Exception e) {
            log.setResponseResult("Error: " + e.getMessage());
            throw e;
        } finally {
            if(!properties.isRamqEnabled()) {
                mongoTemplate.save(log,"logs");
                System.out.println("同步写入");
            }
            else{
                rabbitTemplate.convertAndSend("log-helper",log);
            }
        }

        return result;
    }

    private Map<String, Object> getRequestParams(HttpServletRequest request) {
        Map<String, Object> params = new HashMap<>();
        request.getParameterMap().forEach((key, value) -> params.put(key, String.join(",", value)));
        return params;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
package com.gyo.loghelper.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class RabbitMQEnabledCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 获取 LogHelperProperties 的 ramEnabled 属性值
        boolean ramEnabled = context.getEnvironment().getProperty("log-helper.ramq-enabled", Boolean.class, false);
        return ramEnabled;
    }
}
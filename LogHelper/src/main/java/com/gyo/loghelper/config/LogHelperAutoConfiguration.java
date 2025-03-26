package com.gyo.loghelper.config;

import com.gyo.loghelper.aspect.LogHelperAspect;
import com.gyo.loghelper.util.JwtParser;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import javax.servlet.http.HttpServletRequest;

@Configuration
@ComponentScan(basePackages = "com.gyo.loghelper")
public class LogHelperAutoConfiguration {

    @Bean
    public LogHelperAspect logHelperAspect(LogHelperProperties properties,
                                           MongoTemplate mongoTemplate,
                                           RabbitTemplate rabbitTemplate,
                                           ObjectProvider<HttpServletRequest> requestProvider,
                                           JwtParser jwtParser){
        return new LogHelperAspect(properties, mongoTemplate,rabbitTemplate,requestProvider,jwtParser);
    }

    @Bean
    public LogHelperProperties logHelperConfig() {
        return new LogHelperProperties();
    }
}

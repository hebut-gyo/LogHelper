package com.gyo.loghelper.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.rabbitmq.client.ConnectionFactory;
import lombok.Data;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "log-helper")
@Primary
@Data
public class LogHelperProperties {
    private boolean enabled;
    private String jwtSecret;
    private String mongoCollection;
    private String host;
    private int port;
    private String username;
    private String password;
    private List<String> excludePaths;
    private boolean ramqEnabled;
    private String ramqHost;
    private int ramqPort;
    private String ramqUsername;
    private String ramqPassword;
    private String ramqVirtualHost;
    @Bean
    public MongoClient mongoClient() {
        String connectionString;
        if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
            // 连接 MongoDB，带用户名密码
            connectionString = String.format("mongodb://%s:%s@%s:%d/%s",
                    username, password, host, port, mongoCollection);
        } else {
            // 连接 MongoDB，不带用户名密码
            connectionString = String.format("mongodb://%s:%d/%s", host, port, mongoCollection);
        }

        return MongoClients.create(MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .build());
    }


    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), mongoCollection);
    }

    @Bean
    @Conditional(RabbitMQEnabledCondition.class)
    public CachingConnectionFactory  connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(this.ramqHost);
        connectionFactory.setUsername(this.ramqUsername);
        connectionFactory.setPassword(this.ramqPassword);
        connectionFactory.setPort(this.ramqPort);
        connectionFactory.setVirtualHost(this.ramqVirtualHost);
        return connectionFactory;
    }

    @Bean
    @Conditional(RabbitMQEnabledCondition.class)
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory  connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }
}
package com.gyo.loghelper.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "log-helper")
@Primary
public class LogHelperProperties {
    private boolean enabled;
    private String jwtSecret;
    private String mongoCollection;
    private String host;
    private int port;
    private String username;
    private String password;
    private List<String> excludePaths;

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

    // 生成 Getters 和 Setters
    public void setMongoCollection(String mongoCollection) {
        this.mongoCollection = mongoCollection;
    }
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public String getMongoCollection() {
        return mongoCollection;
    }

    public List<String> getExcludePaths() {
        return excludePaths;
    }

    public void setExcludePaths(List<String> excludePaths) {
        this.excludePaths = excludePaths;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
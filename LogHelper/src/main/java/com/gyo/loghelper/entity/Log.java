package com.gyo.loghelper.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;

@Document(collection = "logs")
public class Log {
        @Id
        private String id;
        private String interfaceName; // 接口名

        private String method;      // HTTP方法
        private Map<String, Object> requestParams;  // 请求参数

        private Map<String,Object> requestData;
        private String responseResult; // 响应结果
        private String token; // token
        private Map<String,Object> userInfo; // 用户信息
        private int createTime; // 创建时间

        private long processTime;

        private String ClientIp;

        private String Description;


        public String getId() {
                return id;
        }

        public void setId(String id) {
                this.id = id;
        }

        public String getInterfaceName() {
                return interfaceName;
        }

        public void setInterfaceName(String interfaceName) {
                this.interfaceName = interfaceName;
        }

        public Map<String, Object> getRequestParams() {
                return requestParams;
        }
        public Map<String, Object> getRequestData() {
                return requestData;
        }

        public void setRequestParams(Map<String, Object> requestParams) {
                this.requestParams = requestParams;
        }
        public void setRequestData(Map<String, Object> requestData) {
                this.requestData = requestData;
        }

        public String getResponseResult() {
                return responseResult;
        }

        public void setResponseResult(String responseResult) {
                this.responseResult = responseResult;
        }

        public String getToken() {
                return token;
        }

        public void setToken(String token) {
                this.token = token;
        }

        public int getCreateTime() {
                return createTime;
        }

        public void setCreateTime(int createTime) {
                this.createTime = createTime;
        }

        public String getMethod() {
                return method;
        }

        public void setMethod(String method) {
                this.method = method;
        }

        public String getClientIp() {
                return ClientIp;
        }

        public void setClientIp(String clientIp) {
                ClientIp = clientIp;
        }


        public Map<String, Object> getUserInfo() {
                return userInfo;
        }

        public void setUserInfo(Map<String, Object> userInfo) {
                this.userInfo = userInfo;
        }

        public long getProcessTime() {
                return processTime;
        }

        public void setProcessTime(long processTime) {
                this.processTime = processTime;
        }

        public String getDescription() {
                return Description;
        }

        public void setDescription(String description) {
                Description = description;
        }
}

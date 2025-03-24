# LogHelper
# 日志助手
**Implementing log management with an annotation**

**一个注解实现日志统一管理**


## Based on SpringBoot and MongoDB

本项目基于SpringBoot框架和MongoDB数据库

## Some Advances

一些优势：

* Easy to operate. After configuration, only one annotation is needed to complete log management.

    操作简便。配置后只需一个注解即可完成日志管理
* Integrate JWT. Parse user information from the token. Support users to rewrite JwtParser.

	整合JWT。从token中解析用户信息。支持用户重写JwtParser。
* Unified storage of logs. Write the collected logs to the MongoDB database.

	日志的统一存储。将收集到的日志写入MongoDB数据库。

	......

* In the next version, I will complete the asynchronous writing of logs and integrate other middleware to support message queuing.
	
	下个版本我将完成日志的异步写入，并整合其他中间件支持消息排队。

* Perhaps a panel for displaying logs is also important.
 
	或许一个用于显示日志的可视化面板也是重要的。

## User Guide
使用方式：

- [1] Download and package the project and incorporate it into your own project (as it has not yet been released to the central repository).
	将项目下载打包并在自己的项目中引入（由于目前还未发布到中心仓库）。

		Pom坐标(尚未发布到中心仓库)

		<dependency>
			<groupId>com.gyo</groupId>
			<artifactId>LogHelper</artifactId>
			<version>0.0.1-SNAPSHOT</version>
		</dependency>
	

- [2] Add configuration in the application.xml file. This project naturally integrates MongoDB and does not require the introduction of MongoDB related configurations again.
	
	在application.yml文件中添加配置。该项目天生整合MongoDB,无需再次引入MongoDB相关配置。

		配置文件
		
		log-helper:
		  enabled: true
		  jwt-secret: gyo5201314
		  mongo-collection: log_helper
		  host: 127.0.0.1
		  port: 27017
		  username: root
		  password: xxxx
		  exclude-paths: /health,/actuator/**


- [3] Due to the fact that the logging business is implemented based on AOP, it is necessary to enable support for AOP programming in the startup class.

	由于日志业务基于AOP实现，因此需要在启动类中开启对AOP编程的支持。

		@SpringBootApplication
		@EnableAspectJAutoProxy
		public class DemoApplication {
		
			public static void main(String[] args) {
				SpringApplication.run(DemoApplication.class, args);
			}
		
		}

- [4] Adding annotations to the business interface allows for the collection of logs for that interface. Annotation parameters are used to mark the interface functionality, and users can design them themselves.

	在业务接口加上注解即可采集该接口的日志。注解参数用于标记该接口功能，用户可自行设计。

	    @PostMapping("/userUpdate")
	    @ApiOperation("更新一条用户信息")
	    @PreAuthorize("hasRole('ROLE_USER')")
	    @Loggable("修改用户信息功能")//核心注解(Core Annotation)
	    public xxx userUpdate(@RequestBody UserInfoRequest  userInfoRequest) {

	        		***相关业务代码***

	        return res;
	    }


- [Addition] In order to avoid the inability to obtain user information due to different JWT encryption methods, the project exposes the JwtParser interface to users, who can rewrite it according to their business needs. The configuration, including user related information, can also be designed by oneself.

	为了避免JWT加密方式的不同导致用户信息无法获取，项目向用户暴露了JwtParser接口。用户可根据业务进行重写。包括用户相关信息的配置，也可以自行设计。

		package com.example.demo.common.config;
		
		import com.example.demo.common.utils.JwtUtils;
		import com.gyo.loghelper.util.JwtParser;
		import io.jsonwebtoken.Claims;
		import io.jsonwebtoken.Jwts;
		import org.springframework.beans.factory.annotation.Autowired;
		import org.springframework.context.annotation.Primary;
		import org.springframework.stereotype.Component;
		
		import java.util.HashMap;
		import java.util.Map;
		@Primary  // 标记为主要 Bean，覆盖默认的解析器
		@Component
		public class MyJwtParser implements JwtParser {
		    @Override
		    public Map<String, Object> parseJwt(String token,String jwtSecret) {
		        Map<String, Object> userInfo = new HashMap<>();
		        if (token != null && token.startsWith("Bearer ")) {
		            try {
		                String jwt = token.substring(7);
						<!-- Jwt的解析 可自定义-->
		                Claims claims = Jwts.parser()
		                        .setSigningKey(jwtSecret)
		                        .parseClaimsJws(jwt)
		                        .getBody();
						<!--用户信息的写入 可自定义 -->
		                userInfo.put("id",claims.get("id"));
		                userInfo.put("username",claims.getSubject());
		
		            } catch (Exception e) {
						<!--用户信息无法获取的异常信息 可自定义 -->
		                userInfo.put("error", "Invalid JWT");
		            }
		        }
		        return userInfo;
		    }
		}

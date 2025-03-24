package com.gyo.loghelper.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtParserImpl implements JwtParser{
    @Override
    public Map<String, Object> parseJwt(String token,String jwtSecret) {
        Map<String, Object> userInfo = new HashMap<>();
        if (token != null && token.startsWith("Bearer ")) {
            try {
                String jwt = token.substring(7);
                Claims claims = Jwts.parser()
                        .setSigningKey(jwtSecret)
                        .parseClaimsJws(jwt)
                        .getBody();
                userInfo.put("id",claims.get("userId"));
                userInfo.put("username",claims.getSubject());

            } catch (Exception e) {
                userInfo.put("error", "Invalid JWT");
            }
        }
        return userInfo;
    }
}

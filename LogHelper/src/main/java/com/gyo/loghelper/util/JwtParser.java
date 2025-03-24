package com.gyo.loghelper.util;

import java.util.Map;

public interface JwtParser {
    Map<String, Object> parseJwt(String token,String jwtSecret);
}

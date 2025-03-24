package com.gyo.loghelper.controller;

import com.gyo.loghelper.aspect.Loggable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {
    @RequestMapping("test")
    @ResponseBody
    @Loggable("打招呼功能")
    public String hello(@Param(value = "param") String param1){
        return "hello";
    }
}

package com.atguigu.eduservice.controller;

import com.atguigu.commonutils.R;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vue-admin-template/eduserviece/user")
@CrossOrigin(methods = {
        RequestMethod.DELETE,RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT
})
public class EduLoginController {
    @PostMapping("login")
    public R login(){
        return R.ok().data("token","admin");
    }
    @GetMapping("info")
    public R info(){
        return R.ok().data("roles","[admin]").data("name","admin").data("avatar","https://img0.baidu.com/it/u=2680183260,1917811104&fm=253&fmt=auto&app=120&f=JPEG?w=650&h=483");
    }
}

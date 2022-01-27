package com.example.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "TestController", tags = "权限测试")
public class TestController {
    /**
     * 测试普通权限
     */
    @GetMapping("/normal/test")
    @PreAuthorize("hasAnyAuthority('ROLE_NORMAL')")
    @ApiOperation(value = "测试普通权限")
    public String test1() {
        return "ROLE_NORMAL /normal/test 接口调用成功";
    }

    /**
     * 测试管理员权限
     */
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/test")
    @ApiOperation(value = "测试管理员权限")
    public String test2() {
        return "ROLE_ADMIN /admin/test 接口调用成功";
    }
}

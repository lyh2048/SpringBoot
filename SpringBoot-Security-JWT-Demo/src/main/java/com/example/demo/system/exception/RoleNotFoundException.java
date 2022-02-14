package com.example.demo.system.exception;

import com.example.demo.system.enums.ErrorCode;

import java.util.Map;

public class RoleNotFoundException extends BaseException{
    public RoleNotFoundException(Map<String, Object> data) {
        super(ErrorCode.Role_NOT_FOUND, data);
    }
}

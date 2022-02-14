package com.example.demo.system.exception;

import com.example.demo.system.enums.ErrorCode;

import java.util.Map;

public class UserNameNotFoundException extends BaseException {
    public UserNameNotFoundException(Map<String, Object> data) {
        super(ErrorCode.USER_NAME_NOT_FOUND, data);
    }
}

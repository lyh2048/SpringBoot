package com.example.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class CommonResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer code;
    private String msg;
    private Object data;

    public static CommonResult success(Integer code, String msg, Object data) {
        CommonResult result = new CommonResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static CommonResult error(Integer code, String msg, Object data) {
        CommonResult result = new CommonResult();
        result.setCode(code);
        result.setMsg(msg);
        result.setData(data);
        return result;
    }

    public static CommonResult error(String msg) {
        return error(400, msg, null);
    }

    public static CommonResult success(Object data) {
        return success(200, "操作成功", data);
    }

    public static CommonResult error(Integer code, String msg) {
        return error(code, msg, null);
    }
}

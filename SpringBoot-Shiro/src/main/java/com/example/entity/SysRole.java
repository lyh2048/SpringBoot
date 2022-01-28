package com.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class SysRole extends Role implements Serializable {
    private static final long serialVersionUID = 1L;

    // 拥有的权限列表
    private List<Permission> permissions;
}

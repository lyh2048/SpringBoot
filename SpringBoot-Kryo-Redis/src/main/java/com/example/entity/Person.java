package com.example.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer age;
}

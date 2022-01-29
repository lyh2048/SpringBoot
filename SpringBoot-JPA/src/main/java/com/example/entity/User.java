package com.example.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "users")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class User {
    /**
     * 用户ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "JDBC")
    private Integer id;
    /**
     * 用户名
     */
    @Column(nullable = false)
    private String username;
    /**
     * 密码
     */
    @Column(nullable = false)
    private String password;
    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private Date createTime;
}

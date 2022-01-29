package com.example.controller;

import com.example.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * UserController
 * restful 风格接口
 */

@RestController
@RequestMapping("/users")
public class UserController {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<User> queryUsers() {
        // 查询所有用户
        String sql = "select * from users";
        return jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper<>(User.class));
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        // 根据主键id查询
        String sql = "select * from users where id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(User.class));
    }

    @PostMapping
    public int addUser(@RequestBody User user) {
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setCreateTime(new Date());
        // 添加用户
        String sql = "insert into users(username,password,create_time) values(?,?,?)";
        return jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getCreateTime());
    }

    @DeleteMapping("/{id}")
    public int deleteUser(@PathVariable Long id) {
        // 根据主键id删除用户
        String sql = "delete from users where id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @PutMapping("/{id}")
    public int updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        // 根据主键id修改用户
        String sql = "update users set username=?,password=? where id = ?";
        return jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), id);
    }
}

package com.example.demo.system.repository;

import com.example.demo.system.entity.Role;
import com.example.demo.system.enums.RoleType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testAddRole() {
        Role[] roles = new Role[] {
          new Role(RoleType.USER.getName(), RoleType.USER.getDescription()),
          new Role(RoleType.TEMP_USER.getName(), RoleType.TEMP_USER.getDescription()),
          new Role(RoleType.MANAGER.getName(), RoleType.MANAGER.getDescription()),
          new Role(RoleType.ADMIN.getName(), RoleType.ADMIN.getDescription())
        };
        for (Role role : roles) {
            roleRepository.save(role);
            log.info(role + "插入成功");
        }
    }
}
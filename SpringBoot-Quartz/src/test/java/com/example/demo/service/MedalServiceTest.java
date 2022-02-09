package com.example.demo.service;

import com.example.demo.entity.Medal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MedalServiceTest {
    @Autowired
    private MedalService medalService;

    @Test
    void testGetMedalData() {
        List<Medal> medalList = medalService.getMedalData();
        medalList.forEach(System.out::println);
    }
}
package com.example.demo.service;

import com.example.demo.entity.Medal;

import java.util.List;

public interface MedalService {
    List<Medal> getMedalData();

    Medal findMedalByName(String name);

    int saveMedal(Medal medal);

    int updateMedal(Medal medal);

    List<Medal> findAll(int orderMode);
}

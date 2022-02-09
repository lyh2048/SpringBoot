package com.example.demo.mapper;

import com.example.demo.entity.Medal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MedalMapper {
    int insertMedal(Medal medal);
    Medal selectMedalByName(String name);
    int updateMedalById(Medal medal);
    List<Medal> selectAll(@Param("orderMode") int orderMode);
}

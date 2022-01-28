package com.example.mapper;

import com.example.entity.ManagerInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ManagerInfoMapper extends ManagerMapper {
    ManagerInfo findByUsername(String username);
}

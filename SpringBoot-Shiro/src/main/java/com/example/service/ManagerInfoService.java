package com.example.service;

import com.example.entity.ManagerInfo;
import com.example.exception.ForbiddenUserException;
import com.example.mapper.ManagerInfoMapper;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ManagerInfoService {
    @Autowired
    private ManagerInfoMapper managerInfoMapper;

    /**
     * 通过用户名查找用户
     */
    public ManagerInfo findByUsername(String username) {
        ManagerInfo managerInfo = managerInfoMapper.findByUsername(username);
        if (null == managerInfo) {
            throw new UnknownAccountException();
        }
        if (managerInfo.getState() == 2) {
            throw new ForbiddenUserException();
        }
        if (managerInfo.getPidsList() == null) {
            managerInfo.setPidsList(Collections.singletonList(0));
        } else if (managerInfo.getPidsList().size() == 0) {
            managerInfo.getPidsList().add(0);
        }
        return managerInfo;
    }
}

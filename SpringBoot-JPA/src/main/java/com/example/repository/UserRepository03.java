package com.example.repository;

import com.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;

public interface UserRepository03 extends PagingAndSortingRepository<User, Integer> {
    User findByUsername(String username);
    Page<User> findByCreateTimeAfter(Date createTime, Pageable pageable);
}

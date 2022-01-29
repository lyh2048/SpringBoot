package com.example.repository;

import com.example.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository04 extends PagingAndSortingRepository<User, Integer> {
    @Query("select u from User u where u.username = ?1")
    User findByUsername01(String username);

    @Query("select u from User u where u.username = :username")
    User findByUsername02(@Param("username") String username);

    @Query(value = "select * from users u where u.username = :username", nativeQuery = true)
    User findByUsername03(@Param("username") String username);

    @Query("update User u set u.username = :username where u.id = :id")
    @Modifying
    int updateUsernameById(@Param("id") Integer id, @Param("username") String username);
}

package com.example.repository;

import com.example.entity.Video;
import org.springframework.data.repository.Repository;

import java.util.List;

public interface VideoRepository extends Repository<Video, Integer> {
    List<Video> findAll();
}

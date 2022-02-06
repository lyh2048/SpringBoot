package com.example.service;

import com.example.entity.Video;
import com.example.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    public List<Video> getVideoList() {
        return videoRepository.findAll();
    }
}

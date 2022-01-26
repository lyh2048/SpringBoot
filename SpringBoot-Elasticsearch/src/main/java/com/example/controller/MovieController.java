package com.example.controller;

import com.example.service.MovieService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class MovieController {
    @Resource
    private MovieService movieService;

    @GetMapping("/search/{keyword}/{page}/{size}")
    public List<Map<String, Object>> search(
            @PathVariable("keyword") String keyword,
            @PathVariable("page") Integer page,
            @PathVariable("size") Integer size
    ) {
        try {
            movieService.parseMovie(keyword);
            return movieService.searchResults(keyword, page, size);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }
}

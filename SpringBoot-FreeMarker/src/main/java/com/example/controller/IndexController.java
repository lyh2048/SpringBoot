package com.example.controller;

import com.example.entity.Video;
import com.example.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private VideoService videoService;

    @RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public String root() {
        return "forward:/index";
    }

    @RequestMapping(value = {"/index"}, method = RequestMethod.GET)
    public String index(Model model) {
        List<Video> videoList = videoService.getVideoList();
        model.addAttribute("title", "视频列表");
        model.addAttribute("videoList", videoList);
        return "index";
    }
}

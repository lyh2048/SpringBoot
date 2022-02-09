package com.example.demo.controller;

import com.example.demo.entity.Medal;
import com.example.demo.service.MedalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class IndexController {
    @Autowired
    private MedalService medalService;

    @GetMapping("/")
    public String root() {
        return "forward:/index";
    }

    @GetMapping("/index")
    public String index(@RequestParam(value = "orderMode", defaultValue = "0") int orderMode, Model model) {
        List<Medal> medalList = medalService.findAll(orderMode);
        model.addAttribute("medalList", medalList);
        return "index";
    }
}

package com.example.controller;

import com.example.entity.Message;
import com.example.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/message")
    public List<Message> getAllMessage() {
        return messageService.getAllMessage();
    }

    @GetMapping("/message/{name}")
    public List<Message> getMessageByName(@PathVariable String name) {
        log.info(cacheManager.toString());
        return messageService.getMessageByName(name);
    }
}

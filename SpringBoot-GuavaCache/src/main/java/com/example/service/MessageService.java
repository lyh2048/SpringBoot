package com.example.service;

import com.example.entity.Message;
import com.example.mapper.MessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    private final Logger log = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageMapper messageMapper;

    @Cacheable(value = "message", key = "#name")
    public List<Message> getMessageByName(String name) {
        List<Message> messageList = messageMapper.getMessageByName(name);
        log.info("从数据库中读取数据");
        return messageList;
    }

    public List<Message> getAllMessage() {
        return messageMapper.getAllMessage();
    }
}

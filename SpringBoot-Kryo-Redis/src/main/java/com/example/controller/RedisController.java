package com.example.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.Person;
import com.example.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Controller
@Slf4j
public class RedisController {
    private static final String PREFIX = "person_";

    @Autowired
    private RedisService redisService;

    /**
     * 拼接key
     */
    private static String key(int id) {
        return PREFIX + id;
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String get(@PathVariable("id") Integer id) {
        Person person = null;
        try {
            person = redisService.getObjects(key(id));
        } catch (Exception e) {
            log.error("get from redis error");
        }
        return null == person ? ("can not get person by id [" + id + "]") : JSONObject.toJSONString(person);
    }

    @RequestMapping(value = "/del/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String del(@PathVariable("id") Integer id) {
        long result = 0L;
        try {
            result = redisService.del(key(id));
        } catch (Exception e) {
            log.error("del from redis error");
        }
        return result > 0 ? ("del success[" + id +"]") : ("can not get person by id [" + id + "]");
    }

    @RequestMapping(value = "/add/{id}/{name}/{age}", method = RequestMethod.GET)
    @ResponseBody
    public String add(
            @PathVariable("id") Integer id,
            @PathVariable("name") String name,
            @PathVariable("age") Integer age
    ) {
        Person person = new Person();
        person.setId(id);
        person.setName(name);
        person.setAge(age);
        String result;
        try {
            redisService.set(key(person.getId()), person);
            result = "save to redis success";
        } catch (Exception e) {
            result = "save to redis fail, " + e;
            log.error("save redis error, ", e);
        }
        return result + "(" + LocalDateTime.now() + ")";
    }
}

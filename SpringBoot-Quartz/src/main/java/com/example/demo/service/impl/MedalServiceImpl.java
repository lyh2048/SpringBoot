package com.example.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.Medal;
import com.example.demo.mapper.MedalMapper;
import com.example.demo.service.MedalService;
import com.example.demo.vo.MedalVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MedalServiceImpl implements MedalService {
    private static final String MEDAL_DATA_URL = "https://api.bilibili.com/x/esports/sports/season/getMedalTable?season_id=1&sort_type=1";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MedalMapper medalMapper;

    @Override
    public List<Medal> getMedalData() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(MEDAL_DATA_URL, String.class);
        HttpStatus code = responseEntity.getStatusCode();
        if (!code.is2xxSuccessful()) {
            log.error("获取数据失败, code=" + code);
            return null;
        }
        String body = responseEntity.getBody();
        JSONObject jsonObject = JSONObject.parseObject(body);
        Object codeObject = jsonObject.get("code");
        if (codeObject == null || Integer.parseInt(codeObject.toString()) != 0) {
            log.error("获取数据失败, code=" + codeObject);
            return null;
        }
        try {
            JSONObject dataJsonObject = (JSONObject) jsonObject.get("data");
            JSONArray table = dataJsonObject.getJSONArray("table");
            List<MedalVo> medalVoList = JSON.parseArray(table.toJSONString(), MedalVo.class);
            log.info("获取数据成功: " + medalVoList.toString());
            List<Medal> result = new ArrayList<>();
            for (MedalVo medalVo : medalVoList) {
                Medal medal = new Medal();
                BeanUtils.copyProperties(medalVo, medal);
                medal.setId(medalVo.getParticipantId());
                medal.setName(medalVo.getParticipantName());
                medal.setImg(medalVo.getParticipantImg());
                result.add(medal);
            }
            return result;
        } catch (Exception e) {
            log.error("解析数据失败: " + e.getMessage());
        }
        return null;
    }

    @Override
    public Medal findMedalByName(String name) {
        return medalMapper.selectMedalByName(name);
    }

    @Override
    public int saveMedal(Medal medal) {
        return medalMapper.insertMedal(medal);
    }

    @Override
    public int updateMedal(Medal medal) {
        if (medal.getId() == null) {
            throw new IllegalArgumentException("参数错误，id不能为空");
        }
        return medalMapper.updateMedalById(medal);
    }

    @Override
    public List<Medal> findAll(int orderMode) {
        if (orderMode != 0 && orderMode != 1) {
            orderMode = 0;
        }
        return medalMapper.selectAll(orderMode);
    }
}

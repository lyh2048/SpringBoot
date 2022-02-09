package com.example.demo.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Medal {
    private Integer id;
    private String name;
    private String img;
    // 金牌数量
    private Integer gold;
    // 银牌数量
    private Integer silver;
    // 铜牌数量
    private Integer bronze;
    // 金牌总数
    private Integer total;
    // 金牌排名
    private Integer goldRank;
    // 总排名
    private Integer totalRank;
}

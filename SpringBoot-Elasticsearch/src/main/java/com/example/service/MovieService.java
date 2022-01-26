package com.example.service;

import com.alibaba.fastjson.JSON;
import com.example.constant.Constants;
import com.example.entity.Movie;
import com.example.utils.HtmlUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class MovieService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    public boolean parseMovie(String keyword) throws IOException {
        GetIndexRequest getIndexRequest = new GetIndexRequest(Constants.INDEX_NAME);
        boolean flag = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        if (!flag) {
            // 创建索引
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(Constants.INDEX_NAME);
            restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        }
        List<Movie> movieList = HtmlUtils.getMovieList(keyword);
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueMillis(1));
        for (Movie movie : movieList) {
            bulkRequest.add(
                    new IndexRequest(Constants.INDEX_NAME)
                            .source(JSON.toJSONString(movie), XContentType.JSON)
            );
        }
        // 提交
        BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        boolean hasFailures = false;
        try {
            hasFailures = !bulkResponse.hasFailures();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasFailures;
    }

    public List<Map<String, Object>> searchResults(String keyword, int page, int size) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        if (page <= 1) {
            page = 1;
        }
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest(Constants.INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(page);
        searchSourceBuilder.size(size);
        // 分词
        QueryStringQueryBuilder queryBuilder = QueryBuilders.queryStringQuery(keyword);
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.timeout(TimeValue.timeValueMillis(1));
        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        // 搜索
        searchRequest.source(searchSourceBuilder);
        // 解析结果
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits()) {
            Map<String, HighlightField> highlightFieldMap = hit.getHighlightFields();
            HighlightField title = highlightFieldMap.get("title");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (title != null) {
                Text[] fragments = title.fragments();
                StringBuilder builder = new StringBuilder();
                for (Text text : fragments) {
                    builder.append(text);
                }
                // 替换
                sourceAsMap.put("title", builder.toString());
            }
            list.add(sourceAsMap);
        }
        return list;
    }
}

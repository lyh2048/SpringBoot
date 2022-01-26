package com.example.utils;

import com.example.entity.Movie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlUtils {
    public static List<Movie> getMovieList(String keyword) throws IOException {
        String url = "https://www.1905.com/search/?q=" + keyword + "&enc=utf-8";
        // 解析网页
        Document document = Jsoup.connect(url).timeout(30000).get();
        Elements movieContent = document.getElementsByClass("new_content");
        List<Movie> movieList = new ArrayList<>();
        for (Element movie : movieContent) {
            String title = movie
                    .getElementsByClass("title-mv")
                    .eq(0)
                    .text();
            String mUrl = movie
                    .getElementsByTag("a")
                    .eq(0)
                    .attr("href");
            if (mUrl.isEmpty() || !mUrl.substring(0, 5).contains("http")) {
                mUrl = "https://www.1905.com/";
            }
            String img = movie
                    .getElementsByTag("img")
                    .eq(0)
                    .attr("src");
            if (img.isEmpty() || !img.substring(0, 5).contains("http")) {
                img = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2619626237,3624368718&fm=15&gp=0.jpg";
            }
            String score = movie
                    .getElementsByClass("movie-pic")
                    .eq(0)
                    .text();
            if (score.isEmpty()) {
                score = "暂无评分";
            }
            String detail = movie
                    .getElementsByTag("p")
                    .eq(0)
                    .text()
                    .replaceAll("简介：", "")
                    .replaceAll("\\[详细\\]", "")
                    .replaceAll("\\[ \\]", "")
                    .trim();

            if (detail.isEmpty()) {
                detail = "暂无简介";
            } else if (detail.length() > 60) {
                detail = detail.substring(0, 60);
                detail += "……";
            }
            movieList.add(new Movie(title, mUrl, img, score, detail));
        }
        return movieList;
    }
}

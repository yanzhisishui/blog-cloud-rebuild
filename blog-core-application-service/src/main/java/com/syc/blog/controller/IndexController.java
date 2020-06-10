package com.syc.blog.controller;


import com.syc.blog.entity.article.Article;
import com.syc.blog.entity.info.Banner;
import com.syc.blog.entity.info.Notice;
import com.syc.blog.entity.info.OnlineUtils;
import com.syc.blog.repository.ArticleRepository;
import com.syc.blog.service.info.BannerService;
import com.syc.blog.service.info.NoticeService;
import com.syc.blog.service.info.OnlineUtilsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Controller
public class IndexController {

    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    BannerService bannerService;
    @Autowired
    NoticeService noticeService;
    @Autowired
    OnlineUtilsService onlineUtilsService;

    @RequestMapping("/")
    public String index(ModelMap map){
        Map<String,Object> params = new HashMap<>();
        List<Article> articleList = queryArticle(params);
        map.put("articleList",articleList);

        List<Banner> bannerList = bannerService.selectList();
        map.put("bannerList",bannerList);

        List<Notice> noticeList = noticeService.selectListLatest(5);
        map.put("noticeList",noticeList);

        List<OnlineUtils> onlineUtilsList = onlineUtilsService.selectList();
        map.put("onlineUtilsList",onlineUtilsList);

        return "index";
    }

    private List<Article> queryArticle(Map<String, Object> params) {
//        String name = params.get("name").toString();
//        QueryBuilder qb1 = QueryBuilders.wildcardQuery("name","*"+name+"*");//名称
//        RangeQueryBuilder qb2 = QueryBuilders.rangeQuery("currentPrice").from(20).to(50);//价格筛选
//        QueryBuilder qb3 = QueryBuilders.matchQuery("categoryId",1);//书籍分类
//
//        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
//        boolQueryBuilder.must(qb1).must(qb2).must(qb3);
//        Pageable pageable = PageRequest.of(1, 10);//分页
//
//        FieldSortBuilder fsb = SortBuilders.fieldSort("currentPrice").order(SortOrder.DESC);//排序
//
//        SearchQuery query = new NativeSearchQueryBuilder()
//                .withQuery(boolQueryBuilder)
//                .withSort(fsb)
//                .withPageable(pageable)
//                .build();
//
//        Page<Book> search = bookRepository.search(query);
        Iterable<Article> all = articleRepository.findAll();
        List<Article> list = new ArrayList<>();
        all.forEach(article -> {
            if(list.size() < 10){
                list.add(article);
            }
        });
        return list;
    }
}

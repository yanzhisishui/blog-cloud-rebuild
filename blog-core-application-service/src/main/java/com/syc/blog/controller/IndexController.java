package com.syc.blog.controller;


import com.alibaba.fastjson.JSON;
import com.syc.blog.constants.Constant;
import com.syc.blog.entity.article.Article;
import com.syc.blog.entity.info.Banner;
import com.syc.blog.entity.info.FriendLink;
import com.syc.blog.entity.info.Notice;
import com.syc.blog.entity.info.OnlineUtils;
import com.syc.blog.entity.user.CardInfo;
import com.syc.blog.repository.ArticleRepository;
import com.syc.blog.service.info.BannerService;
import com.syc.blog.service.info.FriendLinkService;
import com.syc.blog.service.info.NoticeService;
import com.syc.blog.service.info.OnlineUtilsService;
import com.syc.blog.utils.DateHelper;
import com.syc.blog.utils.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class IndexController extends BaseController{

    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    BannerService bannerService;
    @Autowired
    NoticeService noticeService;
    @Autowired
    OnlineUtilsService onlineUtilsService;
    @Autowired
    FriendLinkService friendLinkService;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @RequestMapping("/")
    public String index(ModelMap map, HttpServletRequest request){
        Map<String,Object> params = new HashMap<>();
        List<Article> articleList = queryArticle(params);
        map.put("articleList",articleList);

        List<Banner> bannerList = bannerService.selectList();
        map.put("bannerList",bannerList);

        List<Notice> noticeList = noticeService.selectListLatest(5);
        map.put("noticeList",noticeList);

        List<OnlineUtils> onlineUtilsList = onlineUtilsService.selectList();
        map.put("onlineUtilsList",onlineUtilsList);

        List<FriendLink> friendLinkList = friendLinkService.selectList();
        map.put("friendLinkList",friendLinkList);

        String cardStr = stringRedisTemplate.opsForValue().get(Constant.DICT+"cardInfo");
        CardInfo card = JSON.parseObject(cardStr, CardInfo.class);
        map.put("card",card);

        String ua= request.getHeader("User-Agent");
        if(StringHelper.checkAgentIsMobile(ua)){ //验证手机端登录
            map.put("isMobile",true);
        }
        //判断是否要开启首页灯笼
        if(requireEnableLantern()){
            map.put("enableLantern",true);
        }
        putPageCommon(map);

        buildPagePlugin(1,10,map);
        return "index";
    }

    private boolean requireEnableLantern() {
        String newYearDate = stringRedisTemplate.opsForValue().get(Constant.NEW_YEAR_DATE);
        if(newYearDate == null){
            newYearDate = "2020-01-24";
        }
        Date after30 = DateHelper.getDayRangeBySpecific(newYearDate, 30);
        Date before30 = DateHelper.getDayRangeBySpecific(newYearDate, -30);
        Date now= new Date();
        return now.compareTo(before30) > 0 && now.compareTo(after30) < 0;
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

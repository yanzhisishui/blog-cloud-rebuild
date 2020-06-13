package com.syc.blog.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syc.blog.constants.Constant;
import com.syc.blog.constants.GlobalConstant;
import com.syc.blog.entity.article.Article;
import com.syc.blog.entity.article.ArticleClassify;
import com.syc.blog.entity.comment.UserComment;
import com.syc.blog.entity.info.*;
import com.syc.blog.repository.ArticleRepository;
import com.syc.blog.service.article.ArticleClassifyService;
import com.syc.blog.service.article.ArticleService;
import com.syc.blog.service.comment.UserCommentService;
import com.syc.blog.service.info.*;
import com.syc.blog.utils.DateHelper;
import com.syc.blog.utils.StringHelper;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @Autowired
    ArticleClassifyService articleClassifyService;
    @Autowired
    ArticleService articleService;
    @Autowired
    UserCommentService userCommentService;
    @Autowired
    MicroDiaryService microDiaryService;

    @RequestMapping("/")
    public String index(ModelMap map, HttpServletRequest request){
        Map<String,Object> params = new HashMap<>();
        List<Article> articleList = queryArticle(params);
        map.put("articleList",articleList);

        List<Banner> bannerList = bannerService.selectList();
        map.put("bannerList",bannerList);

        List<Notice> noticeList = noticeService.selectListLatest(5);
        map.put("noticeList",noticeList);

        List<OnlineUtils> onlineUtilsList = onlineUtilsService.selectListLatest(5);
        map.put("onlineUtilsList",onlineUtilsList);

        List<FriendLink> friendLinkList = friendLinkService.selectList();
        map.put("friendLinkList",friendLinkList);

        List<ArticleClassify> articleClassifyList = articleClassifyService.selectListByLevel(1);
        map.put("articleClassifyList",articleClassifyList);

        String ua= request.getHeader("User-Agent");
        if(StringHelper.checkAgentIsMobile(ua)){ //验证手机端登录
            map.put("isMobile",true);
        }
        //判断是否要开启首页灯笼
        if(requireEnableLantern()){
            map.put("enableLantern",true);
        }

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


    @RequestMapping("/learning")
    public String learning(ModelMap map){
        Map<String,Object> params = new HashMap<>();
        List<Article> articleList = queryArticle(params);
        map.put("articleList",articleList);

        List<OnlineUtils> onlineUtilsList = onlineUtilsService.selectListLatest(5);
        map.put("onlineUtilsList",onlineUtilsList);

        List<FriendLink> friendLinkList = friendLinkService.selectList();
        map.put("friendLinkList",friendLinkList);
        //最新文章
        List<Article> articleTitleList  =articleService.selectListLatest(5);
        map.put("articleTitleList",articleTitleList);
        //最新评论
        List<UserComment> latestCommentList = userCommentService.selectListLatest(5);
        map.put("latestCommentList",latestCommentList);
        //技术专栏
        List<ArticleClassify> articleClassifyList = articleClassifyService.selectRandomList(6);
        String[] arr={"out-front", "out-back", "out-left", "out-right", "out-top", "out-bottom"};
        Map<String,ArticleClassify> obj=new HashMap<>();
        for(int i=0;i<articleClassifyList.size();i++){
            ArticleClassify ac = articleClassifyList.get(i);
            if(ac.getName().length() > 6){
                ac.setName(ac.getName().substring(0,5)+"...");
            }
            obj.put(arr[i], ac);
        }
        map.put("zhuanlanList",obj);
        buildPagePlugin(1,10,map);
        return "learning";
    }

    @RequestMapping("/life")
    public String life(ModelMap map,@RequestParam(value = "page",required = false,defaultValue = "1") Integer current){
        if(current < 1){
            current = 1;
        }
        IPage<MicroDiary> page = new Page<>(current,8);
        page = microDiaryService.selectListPage(page);
        map.put("notebookList",page.getRecords());
        buildPagePlugin(current,(int)page.getPages(),map);
        return "life";
    }

    @Autowired
    SkillService skillService;
    @RequestMapping("/aboutme")
    public String aboutme(ModelMap map){
        List<Skill> skillList = skillService.selectList();
        map.put("skillList",skillList);
        //个人信息
        List<Article> grxxList = new ArrayList<>();
        MatchQueryBuilder matchQueryBuilder1 = QueryBuilders.matchQuery("classify.id", 7);
        Iterable<Article> search = articleRepository.search(matchQueryBuilder1);
        search.forEach(grxxList::add);
        map.put("grxxList",grxxList);
        //人生感悟
        List<Article> rsgwList = new ArrayList<>();
        MatchQueryBuilder matchQueryBuilder2 = QueryBuilders.matchQuery("classify.id", 8);
        search = articleRepository.search(matchQueryBuilder2);
        search.forEach(rsgwList::add);
        map.put("rsgwList",rsgwList);
        return "aboutme";
    }

    @RequestMapping("/message")
    public String message(ModelMap map, @RequestParam(value = "page",required = false,defaultValue = "1") Integer current){
        if(current < 1){
            current = 1;
        }
        IPage<UserComment> page = new Page<>(current,GlobalConstant.PAGE_SIZE_COMMENT);
        page = userCommentService.selectFirstLevelCommentPage(page,null,null);
        List<UserComment> firstList = page.getRecords();
        for(UserComment uc : firstList){
            List<UserComment> childrenList = userCommentService.selectSecondLevelComment(null,null,uc.getId());
            uc.setChildrenList(childrenList);
        }
        map.put("firstList",firstList);

        List<ArticleClassify> hotTagList = articleClassifyService.selectHotTagList();
        map.put("hotTagList",hotTagList);
        List<OnlineUtils> onlineUtilsList = onlineUtilsService.selectListLatest(5);
        map.put("onlineUtilsList",onlineUtilsList);
        buildPagePlugin(current, (int)page.getPages(),map);
        String howBuildBlogDesc = stringRedisTemplate.opsForValue().get(Constant.HOW_BUILD_BLOG_DESC);
        map.put("howBuildBlogDesc",howBuildBlogDesc);
        return "message";
    }
}

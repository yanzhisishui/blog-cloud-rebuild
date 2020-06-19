package com.syc.blog.controller.article;

import com.alibaba.fastjson.JSON;
import com.syc.blog.constants.Constant;
import com.syc.blog.controller.BaseController;
import com.syc.blog.entity.article.Article;
import com.syc.blog.entity.article.ArticleClassify;
import com.syc.blog.entity.info.OnlineUtils;
import com.syc.blog.repository.ArticleRepository;
import com.syc.blog.service.article.ArticleClassifyService;
import com.syc.blog.service.info.OnlineUtilsService;
import com.syc.blog.utils.StringHelper;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/article")
public class ArticleController extends BaseController {
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    OnlineUtilsService onlineUtilsService;
    @Autowired
    ArticleClassifyService articleClassifyService;
    @RequestMapping("/{id}")
    public String chapter(@PathVariable("id") Integer id, ModelMap map,
                          @RequestParam(value = "page",required = false,defaultValue = "1") Integer page, HttpServletRequest request){

        Article article = articleRepository.findById(id).orElse(null);
        map.put("article",article);
        map.put("pre",recursionFindArticle(id,0));
        map.put("next",recursionFindArticle(id,1));

        List<OnlineUtils> onlineUtilsList = onlineUtilsService.selectListLatest(5);
        map.put("onlineUtilsList",onlineUtilsList);
        List<ArticleClassify> articleClassifyList = articleClassifyService.selectListByLevel(1);
        map.put("articleClassifyList",articleClassifyList);

        String ua= request.getHeader("User-Agent");
        if(StringHelper.checkAgentIsMobile(ua)){ //验证手机端登录
            map.put("isMobile",true);
        }
        byte type = Constant.USER_COMMENT_TYPE_ARTICLE;
        getCurrentCommentsListPage(map,page,id,type);

        return "article";
    }

    /**
     * flag = 0 :反向查找
     * flag = 1 :正向查找
     * 递归查找上一篇下一篇文章
     * */
    Article recursionFindArticle(int id,int flag){
        if(id < 1 || id > 100){
            Article a = new Article();
            a.setTitle("");
            return a;
        }
        id = flag == 0 ? id - 1 : id +1;
        Article article = articleRepository.findById(id).orElse(null);
        if(article == null){
            return recursionFindArticle(id,flag);
        }
        return article;
    }

}

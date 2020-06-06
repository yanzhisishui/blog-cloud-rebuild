package com.syc.blog.controller.article;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syc.blog.entity.article.Article;
import com.syc.blog.mapper.article.ArticleMapper;
import com.syc.blog.utils.JsonHelper;
import com.syc.blog.utils.ResultHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    ArticleMapper articleMapper;
    @RequestMapping("/manage")
    public String manage(){
        return "article/manage";
    }

    @RequestMapping("/queryListPage")
    @ResponseBody
    public String queryListPage(@RequestParam("page") Integer page, @RequestParam("limit") Integer pageSize){
        IPage<Article> iPage = new Page<>(page,pageSize);
        IPage<Article> bannerIPage = articleMapper.selectPage(iPage, Wrappers.<Article>lambdaQuery().eq(Article::getArchive,0));
        return JsonHelper.objectToJsonForTable(bannerIPage.getRecords(),bannerIPage.getTotal());
    }

    @RequestMapping("/save")
    @ResponseBody
    public String save(Article article){
        article.setDateInsert(new Date());
        int row = articleMapper.insert(article);
        ResultHelper result= row == 0 ? ResultHelper.wrapErrorResult(1,"添加技能失败") : ResultHelper.wrapSuccessfulResult(null);
        return JSON.toJSONString(result);
    }

    @RequestMapping("/update")
    @ResponseBody
    public String update(Article article){
        article.setDateUpdate(new Date());
        int row = articleMapper.updateById(article);
        ResultHelper result= row == 0 ? ResultHelper.wrapErrorResult(1,"更新技能失败") : ResultHelper.wrapSuccessfulResult(null);
        return JSON.toJSONString(result);
    }
    @RequestMapping("/add")
    public String add(){
        return "article/add";
    }

    @RequestMapping("/edit")
    public String edit(@RequestParam("id") Integer id, ModelMap map){
        Article article=articleMapper.selectById(id);
        map.put("article",article);
        return "onlineutils/article/edit";
    }

    @RequestMapping("/delete")
    @ResponseBody
    public String delete(@RequestParam("id") Integer id){
        Article article = new Article();
        article.setDateUpdate(new Date());
        article.setId(id);
        article.setArchive((byte)1);
        int row=articleMapper.updateById(article);
        ResultHelper result= ResultHelper.wrapSuccessfulResult(null);
        return JSON.toJSONString(result);
    }
}

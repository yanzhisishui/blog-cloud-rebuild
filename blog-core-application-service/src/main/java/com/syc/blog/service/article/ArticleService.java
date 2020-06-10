package com.syc.blog.service.article;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.syc.blog.entity.article.Article;
import com.syc.blog.mapper.article.ArticleMapper;
import com.syc.blog.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

@Service
public class ArticleService {
    @Autowired
    ArticleRepository articleRepository;
    @Autowired
    ArticleMapper articleMapper;
    public void initRepository() {
        List<Article> list =  articleMapper.selectList(Wrappers.<Article>lambdaQuery().eq(Article::getArchive,0));
        articleRepository.saveAll(list);
    }
}

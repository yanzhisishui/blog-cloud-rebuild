package com.syc.blog.mapper.article;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.syc.blog.entity.article.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    IPage<Article> queryListPage(IPage<Article> iPage, @Param("params") Map<String, Object> params);
}

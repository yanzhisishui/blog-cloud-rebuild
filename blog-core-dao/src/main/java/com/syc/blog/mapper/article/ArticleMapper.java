package com.syc.blog.mapper.article;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syc.blog.entity.article.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
}

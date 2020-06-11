package com.syc.blog.mapper.article;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.syc.blog.entity.article.Article;
import com.syc.blog.entity.article.ArticleClassify;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleClassifyMapper extends BaseMapper<ArticleClassify> {
    @Select("select id,name from article_classify  where parent_id = 1  order by rand() limit #{num} ")
    List<ArticleClassify> selectRandomList(@Param("num") int i);
}

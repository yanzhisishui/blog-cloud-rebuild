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

    @Select("select u.*,(select count(*) from article_classify uu where uu.parent_id = u.id) as directChildrenCount from article_classify u  where u.level = #{level}")
    List<ArticleClassify> selectListByLevel(@Param("level") int i);

    @Select("select u.*,(select count(*) from article_classify uu where uu.parent_id = u.id) as directChildrenCount from article_classify u  where u.parent_id = #{parentId}")
    List<ArticleClassify> selectListByParentId(@Param("parentId") Integer id);
}

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

    @Select("select t1.*,count(*) as directChildrenCount from article_classify t1 LEFT JOIN " +
            "article_classify t2 on t1.id = t2.parent_id where t1.level = #{level} GROUP BY t1.id")
    List<ArticleClassify> selectListByLevel(@Param("level") int i);

    @Select("select t1.*,count(*) as directChildrenCount from article_classify t1 LEFT JOIN " +
            "article_classify t2 on t1.id = t2.parent_id where t1.parent_id = #{parentId} GROUP BY t1.id")
    List<ArticleClassify> selectListByParentId(@Param("parentId") Integer id);
}

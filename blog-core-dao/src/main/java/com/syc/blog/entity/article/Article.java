package com.syc.blog.entity.article;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文章
 * */
//@Document(indexName = "blog",type = "article")
@Data
@TableName("article")
public class Article implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    private Date dateInsert;
    private Date dateUpdate;
    private Byte archive;//删除标志 0:未删除  1：逻辑删除
    private String description;//文章描述 用于首页展示，长度（250-270）
    private Integer classifyId;//类别ID
    private Integer browser;//浏览人数
    private String content;//内容(样式、图片、小标题、HTML标签都包含在内)
    private String title;//文章标题
    private String bread;
    private String keyword;
    private Integer userId;//发布者id
    private String imageUrl;//图片地址
}

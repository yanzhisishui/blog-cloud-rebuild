package com.syc.blog.service.article;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.syc.blog.constants.RedisConstant;
import com.syc.blog.entity.article.Article;
import com.syc.blog.entity.article.UserPraiseArticle;
import com.syc.blog.mapper.article.ArticleMapper;
import com.syc.blog.mapper.article.UserPraiseArticleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;

@Service
@Slf4j
public class UserPraiseArticleService {
    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    UserPraiseArticleMapper userPraiseArticleMapper;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    //同步redis数据到mysql
    @Transactional
    public void syncRedisData(Map<Object, Object> map) {

        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            String articleId = entry.getKey().toString();
            Boolean flag = stringRedisTemplate.hasKey(RedisConstant.ARTICLE_SYNC_PRAISE_ARTICLE + articleId);
            if(flag != null && flag){
                //已经同步过了,同步下一篇
                continue;
            }
            HashSet<String> set = JSON.parseObject(entry.getValue().toString(), HashSet.class);
            Article a = new Article();
            a.setId(Integer.parseInt(articleId));
            a.setPraise(set.size());
            articleMapper.updateById(a);//更新文章收藏数

            List<UserPraiseArticle> list = new ArrayList<>();
            for (String userId : set) {
                UserPraiseArticle upa = new UserPraiseArticle();
                upa.setUserId(Integer.parseInt(userId));
                upa.setArticleId(Integer.parseInt(articleId));
                upa.setDateInsert(new Date());
                list.add(upa);
            }
            int row = userPraiseArticleMapper.saveList(list);
            stringRedisTemplate.opsForValue().set(RedisConstant.ARTICLE_SYNC_PRAISE_ARTICLE+articleId,articleId);//标记该文章已同步过
            log.info("文章:{} 同步完成-----",articleId);
        }
    }
}

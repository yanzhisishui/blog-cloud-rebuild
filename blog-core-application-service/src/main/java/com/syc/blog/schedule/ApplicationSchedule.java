package com.syc.blog.schedule;

import com.syc.blog.constants.RedisConstant;
import com.syc.blog.entity.article.UserPraiseArticle;
import com.syc.blog.service.article.ArticleService;
import com.syc.blog.service.article.UserPraiseArticleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;

@Configuration
@Slf4j
public class ApplicationSchedule {

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    ArticleService articleService;
    @Autowired
    UserPraiseArticleService userPraiseArticleService;

    /**
     * 定时同步redis文章点赞的数据到MySQL,每小时执行一次
     * */
    @Scheduled(cron = "0 0 0/1 * * ?")
    //@Scheduled(cron = "0/10 * * * * ?")
    public void redisArticlePraiseToMySQL(){
        log.info("同步redis文章点赞数据开始");
        userPraiseArticleService.syncRedisData();
        log.info("同步redis文章点赞数据结束");
    }
}

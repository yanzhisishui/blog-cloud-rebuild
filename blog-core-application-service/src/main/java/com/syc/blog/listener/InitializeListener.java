package com.syc.blog.listener;

import com.syc.blog.constants.Constant;
import com.syc.blog.entity.config.BaseConfig;
import com.syc.blog.entity.user.CardInfo;
import com.syc.blog.service.article.ArticleService;
import com.syc.blog.service.config.BaseConfigService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class InitializeListener implements ServletContextListener {

    @Autowired
    ArticleService articleService;
    @Autowired
    BaseConfigService baseConfigService;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //初始化数据到ES
        articleService.initRepository();
        //初始化数据到Redis
        baseConfigService.initRedis();

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

}

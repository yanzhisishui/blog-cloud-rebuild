package com.syc.blog.listener;

import com.syc.blog.service.article.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class InitializeListener implements ServletContextListener {

    @Autowired
    ArticleService articleService;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        //初始化数据到ES
        articleService.initRepository();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

}

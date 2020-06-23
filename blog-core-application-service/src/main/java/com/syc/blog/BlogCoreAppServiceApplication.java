package com.syc.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
import org.springframework.session.web.http.CookieHttpSessionIdResolver;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HttpSessionIdResolver;

@SpringBootApplication
@EnableRabbit //开启rabbitMQ注解
@ServletComponentScan
@MapperScan(basePackages = {"com.syc.blog.mapper"})//扫描@Mapper注解
@EnableRedisHttpSession //交给redis
//@EnableCaching
public class BlogCoreAppServiceApplication {
    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors","false");//解决redis和elasticsearch冲突
        SpringApplication.run(BlogCoreAppServiceApplication.class,args);
    }

    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        CookieHttpSessionIdResolver cookieHttpSessionIdResolver = new CookieHttpSessionIdResolver();
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();

        //不加前缀，实现子域名session共享
        cookieSerializer.setDomainName("sunyuchao.com");
        cookieSerializer.setCookiePath("/");
        cookieSerializer.setUseBase64Encoding(false);
        cookieHttpSessionIdResolver.setCookieSerializer(cookieSerializer);
        return cookieHttpSessionIdResolver;
    }

}

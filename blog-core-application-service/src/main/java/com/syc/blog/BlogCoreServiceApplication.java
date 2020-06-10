package com.syc.blog;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
//@EnableRabbit //开启rabbitMQ注解
@ServletComponentScan
@MapperScan(basePackages = {"com.syc.blog.mapper"})//扫描@Mapper注解

//@EnableRedisHttpSession //交给redis
//@EnableCaching
public class BlogCoreServiceApplication {
    public static void main(String[] args) {
        System.setProperty("es.set.netty.runtime.available.processors","false");//解决redis和elasticsearch冲突
        SpringApplication.run(BlogCoreServiceApplication.class,args);
    }

}

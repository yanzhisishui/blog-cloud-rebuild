package com.syc.blog.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syc.blog.constants.Constant;
import com.syc.blog.entity.comment.UserComment;
import com.syc.blog.entity.user.CardInfo;
import com.syc.blog.service.comment.UserCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;

public class BaseController {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    UserCommentService userCommentService;

    public void getCurrentCommentsListPage(ModelMap map,Integer page,Integer bindId,byte type){
        if(page < 1) { //页数过小
            page=1;
        }
        Integer pageSize= Constant.PAGE_SIZE_COMMENT;
        IPage<UserComment> commentIPage = new Page<>(page,pageSize);
        commentIPage = userCommentService.selectFirstLevelCommentPage(commentIPage,type, bindId);
        List<UserComment> firstList = commentIPage.getRecords();
        for(UserComment uc : firstList){
            List<UserComment> childrenList = userCommentService.selectSecondLevelComment(type,bindId,uc.getId());
            uc.setChildrenList(childrenList);
        }
        Integer pageTotal = (int) commentIPage.getTotal();
        if(page > pageTotal && pageTotal > 0) { //超过最大页数
            page = pageTotal;
        }
        buildPagePlugin(page,pageTotal,map);
        map.put("firstList",firstList);
    }


    void buildPagePlugin(int page, int pageTotal, ModelMap map) {
        List<Integer> pageNumList=new ArrayList<>();//构造页码
        for(int i=1;i<=pageTotal;i++){
            pageNumList.add(i);
        }
        map.put("page",page);
        map.put("pageNumList",pageNumList);
        map.put("pageTotal",pageTotal);
    }

    /**
     * 注入页面的redis常用值
     * */
    public void putPageCommon(ModelMap map){
        //网站logo
        String logoName = stringRedisTemplate.opsForValue().get(Constant.DICT_LOGO_URL);
        map.put("logoUrl",logoName);
        //图标地址
        String iconfontUrl = stringRedisTemplate.opsForValue().get(Constant.DICT_ICONFONT_URL);
        map.put("iconfontUrl",iconfontUrl);

        String cardStr = stringRedisTemplate.opsForValue().get(Constant.DICT+"cardInfo");
        CardInfo card = JSON.parseObject(cardStr, CardInfo.class);
        map.put("card",card);

    }

}

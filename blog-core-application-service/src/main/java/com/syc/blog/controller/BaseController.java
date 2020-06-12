package com.syc.blog.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.syc.blog.constants.Constant;
import com.syc.blog.constants.RedisConstant;
import com.syc.blog.entity.comment.UserComment;
import com.syc.blog.entity.user.CardInfo;
import com.syc.blog.entity.user.User;
import com.syc.blog.service.comment.UserCommentService;
import com.syc.blog.utils.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.ui.ModelMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
        String logoName = stringRedisTemplate.opsForValue().get(RedisConstant.DICT_LOGO_URL);
        map.put("logoUrl",logoName);
        //图标地址
        String iconfontUrl = stringRedisTemplate.opsForValue().get(RedisConstant.DICT_ICONFONT_URL);
        map.put("iconfontUrl",iconfontUrl);

        String cardStr = stringRedisTemplate.opsForValue().get(RedisConstant.DICT_CARD_INFO);
        CardInfo card = JSON.parseObject(cardStr, CardInfo.class);
        map.put("card",card);

    }


    /**
     * 获取当前登录的用户
     * */
    public User getLoginUser(HttpServletRequest request){
        HttpSession session = request.getSession();
        Object currentAdminUser = session.getAttribute(Constant.USER_LOGIN_SESSION_KEY);
        if(currentAdminUser == null){
            return null;
        }
        return (User) currentAdminUser;
    }

    /**
     * 评论检查
     * */
    public String commentCheck(UserComment comment, HttpServletRequest request) {
        String content=comment.getContent();
        if(content == null || content.trim().length() == 0){
            return "请填写评论内容";
        }
        if(content.length() > 150){
            return  "评论不得超过150个字符";
        }
        User loginUser = getLoginUser(request);
        if(StringHelper.hasIllegal(content)){
            return "请文明发言";
        }
        if(loginUser == null ){ //生产环境必须要登录
            return "请先登录";
        }
        if(comment.getUserId().equals(comment.getCommentedUserId())){
            return "不能自己回复自己";
        }
        return null;
    }

}

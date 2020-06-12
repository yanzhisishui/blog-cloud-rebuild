package com.syc.blog.controller.feedback;

import com.alibaba.fastjson.JSON;
import com.syc.blog.controller.BaseController;
import com.syc.blog.entity.user.User;
import com.syc.blog.feedback.FeedBack;
import com.syc.blog.service.feedback.FeedBackService;
import com.syc.blog.utils.ResultHelper;
import com.syc.blog.utils.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
@RequestMapping("/feedback")
public class FeedBackController extends BaseController {
    @Autowired
    FeedBackService feedBackService;


    @RequestMapping
    public String feedback(){
        return "feedback";
    }

    @RequestMapping("/save")
    @ResponseBody
    public String save(FeedBack feedBack, HttpServletRequest request){
        User loginUser = getLoginUser(request);
        ResultHelper result;
        if(loginUser == null){
            result= ResultHelper.wrapErrorResult(1,"请先登录");
            return JSON.toJSONString(result);
        }
        boolean illegal = StringHelper.hasIllegal(feedBack.getContent());
        if(illegal){
            result= ResultHelper.wrapErrorResult(3,"请文明发言");
            return JSON.toJSONString(result);
        }
        feedBack.setUserId(loginUser.getId());
        feedBack.setDateInsert(new Date());
        int row=feedBackService.save(feedBack);
        result=row == 0 ? ResultHelper.wrapErrorResult(2,"反馈失败") : ResultHelper.wrapSuccessfulResult(null);
        return JSON.toJSONString(result);
    }
}
